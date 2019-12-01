package battle;

import bullet.Bullet;
import creature.*;
import creature.enumeration.Camp;
import creature.enumeration.Direction;
import formation.Formation;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.*;

/**
 * @author csl
 * @date 2019/11/24 21:47
 */
public class BattleController implements Config {
    ExecutorService pool;//线程池
    int n = 0;
    @FXML
    private BorderPane pane;//主面板
    @FXML
    private Canvas canvas;//在构造函数里面canvas还是null,我也不知道什么时候初始化完成的
    private GraphicsContext gc;//用于在canvas上直接绘图
    Map map;//地图
    BattleState battleState;//战斗状态，由多个线程共享，指示子弹移动、ui刷新线程是否要退出
    private Image evilWinImage;
    private Image justiceWinImage;
    private ArrayList<Huluwa> huluwas = new ArrayList<Huluwa>();
    private GrandPa grandPa;
    private ArrayList<Evil> evils = new ArrayList<>();
    private Scorpion scorpion;
    private Snake snake;
    private LinkedList<Bullet> bullets = new LinkedList<>();//由于bullets需要经常增删，用链表
    private BulletManager bulletManager;
    private ObjectOutputStream writer;//每次刷新就向文件写
    private ObjectInputStream reader;

    public BattleController() {
    }

    @FXML
    private void initialize() {
        pane.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
//                System.out.println(event.getCode());
                if(event.getCode() == KeyCode.SPACE){
                    startGame();
                }
                else if(event.getCode() == KeyCode.F && battleState.isBattleStarted() == false){
                    //在战斗还没开始可以变换阵型
                    Formation.transFormToNextFormation(map,scorpion,snake,evils,bullets);
                    map.display(false);//writer已经关闭
                }
                else if(event.getCode() == KeyCode.UP && battleState.isBattleStarted()){
                    grandPa.addDirection(Direction.UP);
                }
                else if(event.getCode() == KeyCode.DOWN && battleState.isBattleStarted()){
                    grandPa.addDirection(Direction.DOWN);
                }
                else if(event.getCode() == KeyCode.RIGHT && battleState.isBattleStarted()){
                    grandPa.addDirection(Direction.RIGHT);
                }
                else if(event.getCode() == KeyCode.LEFT && battleState.isBattleStarted()){
                    grandPa.addDirection(Direction.LEFT);
                }
            }
        });
        Platform.runLater(() -> pane.requestFocus());//否则键盘无效
        //战斗结果图像初始化
        URL url = this.getClass().getClassLoader().getResource("pictures/" + "EvilWinner.png");
        evilWinImage = new Image(url.toString());
        url = this.getClass().getClassLoader().getResource("pictures/" + "JusticeWinner.png");
        justiceWinImage = new Image(url.toString());
        //完成画布、地图和葫芦娃等的初始化
        canvas.setWidth(CANVAS_WIDTH);//960
        canvas.setHeight(CANVAS_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        battleState = new BattleState();
        map = new Map(battleState, MAP_REFRESH_RATE, gc,bullets);
        bulletManager = new BulletManager(map,bullets,battleState);
        scorpion = new Scorpion(map,bullets);
        snake = new Snake(map,bullets);
        Formation.transFormToNextFormation(map,scorpion,snake,evils,bullets);
        initGrandPa();
        initHuluwas();//创建葫芦娃
        map.display(false);
    }

    private void initGrandPa(){
        URL url = this.getClass().getClassLoader().getResource("pictures/grandPa.png");
        Image image = new Image(url.toString());
        grandPa = new GrandPa(map,image,"GrandPa",bullets);
    }

    private void initHuluwas() {
        //葫芦娃的初始化，创建了七个葫芦娃，并且排阵
        for (int i = 1; i <= 7; ++i) {
            URL url = this.getClass().getClassLoader().getResource("pictures/" + i + ".png");
            Image image = new Image(url.toString());
            huluwas.add(new Huluwa(map, image, "h",i,bullets));
        }
        transFormChangShe();
    }

    public void arrangeHuluwas(){
        //将葫芦娃排列为长蛇阵型
        //正常情况下，这是在游戏开始之前执行的，此时只有一个main线程，因此不存在对map的竞争访问
        //葫芦娃都先恢复正常状态:aive,Hp...
        for(Huluwa huluwa:huluwas){
            huluwa.resetState();
        }
        grandPa.resetState();
        //重新排列
        synchronized (map){//其实这个synchronized没必要
            transFormChangShe();
        }
    }

    private void transFormChangShe() {
        //将葫芦娃重置为长蛇阵型，需要修改map和huluwa数组,调用Creature的moveTo方法
        int y = NUM_COLUMNS/4;
        int x = NUM_ROWS/2 - 3;
        grandPa.moveTo(6,2);
        for (Huluwa huluwa : huluwas) {
            huluwa.moveTo(x, y);
            ++x;
        }
    }

    public void startGame(){
        if(battleState.isBattleStarted())//战斗已经开始
            return;
        //序列化输出文件
        try {
            //缓冲一下，加快速度
            writer = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("log")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        map.setWriter(writer);
        System.out.println("after set:"+writer);
        //按下空格,开始游戏
        battleState.setStarted(true);
        //葫芦娃线程start
        //由于之前是用shutDownNow退出的，如果还是用之前的线程池再execute会出错，因此重新申请一个线程池
        pool = Executors.newCachedThreadPool();
        for(Evil evil:evils){
            pool.execute(evil);
        }
        for(Huluwa huluwa:huluwas){
            pool.execute(huluwa);
        }
        pool.execute(grandPa);
        pool.execute(scorpion);
        pool.execute(snake);
        pool.execute(map);//战场、子弹刷新线程start
        pool.execute(bulletManager);//负责子弹移动、出界、伤害
        //用一个线程侦听战斗是否结束，while(notEnded) wait(); map的display()检测双方人数，若一方=0，则notifyAll()结束map线程,
        new Thread(() -> {//用lambda表达式代替匿名内部类
            synchronized (battleState){
                while (battleState.isBattleStarted()){//等待战斗结束
                    try {
                        battleState.wait();//等待battleState的锁，而不是忙等待监听
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            gameOver();//现在这个侦听线程也要结束了
        }).start();//这个侦听线程不经过pool控制
    }

    public void gameOver(){
        //这个函数在侦听线程中被调用
        //失败一方的所有生物线段都因为alive = false 导致线程退出
        //胜利一方的所有生物线程、map刷新线程、bulletManager线程都可能还在运行
        //因此需要shutDownNow向所有线程发送interrupt()让他们退出
        battleState.setStarted(false);
        System.out.println(n+"th game over");
        pool.shutdownNow();
        //现在只剩一个主线程了
        synchronized (map){
            map.clearMap();
        }
        arrangeHuluwas();
        transFormChangShe();
        bulletManager.clearBullets();

        scorpion.resetState();
        snake.resetState();
        grandPa.resetState();
        grandPa.clearDirection();//可能错误的原因是：比赛结束，clearDirections的时候，按下了方向键，这样就同时修改directions
        Formation.transFormToNextFormation(map,scorpion,snake,evils,bullets);
        //一场战斗结束，应该显示游戏结果，三秒后再显示战斗初始界面
        //先显示战斗结果图片
        if(battleState.getWinner() == Camp.JUSTICE){
            gc.drawImage(justiceWinImage,0,0,CANVAS_WIDTH,CANVAS_HEIGHT);
        }
        else{
            gc.drawImage(evilWinImage,0,0,CANVAS_WIDTH,CANVAS_HEIGHT);
        }
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        map.display(false);//这时候已经writer已经关闭
        //test
        //如果在侦听线程sleep(2)的时候案例空格键，那么主线程就会调用strtGame()，就会显示活跃线程数不为0，但是问题不大
        ++n;
        int threadCount = ((ThreadPoolExecutor)pool).getActiveCount();
        System.out.println("game "+n+",线程池中还有"+threadCount+"个活跃线程");
//        startGame();
    }
//    public void pauseGame(){
//        //按下
//    }
}
