# Java 葫芦娃大作业 
<center>
171860525 计算机科学与技术系 陈善梁  
邮箱: 171860525@smail.nju.edu.cn
</center>  

## 项目框架
五个package:battle,bullet,creature,formation和record，它们的职责如下:  
|package|职责|
|:----:|:----|
|battle|1. (BattleController.java) 负责UI组件(BorderPane、Canvas等)的初始化、战场元素(葫芦娃、妖精、子弹列表、子弹管理器、地图)的初始化、与记录有关的输入输出流(writer、reader)的声明、用于UI界面刷新的TimeLine的声明、线程池的声明 <br> 2. (BattleController.java) 监听键盘事件:空格键开始/暂停/继续游戏，F键改变阵型、L选择记录文件回放以及方向键控制移动等 <br> 3. (BattleController.java)实现对于键盘事件的相应函数，如startGame()、gameOver()、pauseGame()、continueGame()等。<br> 4. (Map.java)定义重要的战场地图Map类，负责控制访问和修改战场某一位置的生物、显示战斗过程中的每一帧画面、显示回放过程中的每一帧画面。该类由所有生物对象共享。<br> 5. (Config.java) 配置战场规格、不同生物特性，，如战场大小、行列数、生物攻击力等 <br> 6. (BattleState.java) 定义全局的BattleState类，表示当前状态:未开始、开始、暂停等。所有战场元素共享该对象。|
|creature|定义所有的生物，基类Creature、葫芦娃类Huluwa、老爷爷GrandPa和妖精Evil等|
|bullet|定义所有的子弹类及其工厂类。|
|formation|定义阵法Formation类，实现所有改变妖精阵型的方法|
|record|定义游戏记录的三个类:BulletRecord(记录当前某一个子弹及其位置、伤害等信息)、CreatureRecord(记录当前某一个生物及其位置、状态信息)、Record(记录当前帧所有的子弹和生物)|
## 面向对象的思想
继承、封装(Map的访问)、多态(不同的生物move、attack的方法不同)
设计原则：重点 1.观察者模式:匿名线程wait battlestate等待map将state置为结束并且notify。 2. 工厂模式：不同的生物发射不同的子弹，但是在形式上都是通过相同的BulletGenerator产生的子弹，也是一种多态的体现。
## 多线程的协同问题
1. 简单的synchronized方式，如果嵌套地使用synchronized，仍然有可能产生死锁，我采用的方案是如果多个线程都要同时得到不同对象的锁，要以相同的顺序synchronized这些对象。防止循环等待。
2. wait、notify。比较满意的地方：调用gameOver()的方式。在gameStart后，生物线程、子弹线程、Timeline都开始执行了，同时用一个单独的匿名线程：
```
new Thread(() -> {//用lambda表达式代替匿名内部类
            synchronized (battleState) {//观察者模式
                while (battleState.isBattleStarted()) {//等待战斗结束
                    try {
                        battleState.wait();//等待battleState的锁，而不是忙等待监听
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            gameOver();//现在这个侦听线程也要结束了
        }).start();//这个侦听线程不经过pool控制
```
来等待战斗结束的通知。而这个通知是谁发出的呢？
而比赛结束的判断是在timeLine调用map.display()时，发现一方数量为0，这时候:
```
if (numEvilLeft == 0 || numJusticeLeft == 0) {
            battleState.setStarted(false);
            if (numEvilLeft == 0) {//设置战斗胜利者并且绘制
                battleState.setWinner(Camp.JUSTICE);
                gc.drawImage(justiceWinImage, 0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
            } else {
                battleState.setWinner(Camp.EVIL);
                gc.drawImage(evilWinImage, 0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
            }
            synchronized (battleState) {
                battleState.notifyAll();//唤醒侦听线程
            }
        }
```
我个人觉得这种方式比简单的用忙等待的方式检测battleState好。
## 序列化的使用