package creature;

import battle.Config;
import battle.Map;
import bullet.*;
import creature.enumeration.Camp;
import creature.enumeration.Direction;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
 * @author csl
 * @date 2019/11/24 20:49
 */
public abstract class Creature implements Runnable, Config, Serializable {
    //只有阵营、存活状态、图像(也许可以用map统一保存，但是先存吧)、hp需要保存
    protected int MAX_HP;
    protected int currentHP;
    protected Camp camp;
    protected boolean alive;
    protected boolean lastTimeSent;//上一次是否发射了子弹
    protected Image image;
    protected Random random;
    protected Position position;
    protected Map map;
    protected String name;
    protected int attackValue;//攻击力 > 50
    protected int defenseValue;//防御力 < 50
    protected int moveRate;//速度,sleepTime = 1000ms/moveRate;
    protected LinkedList<Bullet> bullets;
    protected BulletGenerator<Bullet> bulletGenerator;//工厂模式

    public Creature() {
    }

    public Creature(Map map, LinkedList<Bullet> bullets) {
        camp = Camp.JUSTICE;
        random = new Random();
        alive = true;
        MAX_HP = DEFAULT_MAX_HP;
        currentHP = MAX_HP;
        attackValue = DEFAULT_ATTACK_VALUE;//实际上不同的人物要有不同的武力值，可以在Config中设置不同的武力值
        defenseValue = DEFAULT_DEFENSE_VALUE;
        moveRate = DEFAULT_MOVE_RATE;//0.5 s
        this.map = map;
        position = new Position();
        this.bullets = bullets;
        lastTimeSent = false;
    }

    public void attack()//不同的生物有不同的攻击方式：近距离 or 子弹
    {
        //TODO search the map and found Enimies and send bullets
        //首先寻找九宫格之内的敌人
        //算了，先发射水平子弹吧
        //寻找水平方向的敌人
        synchronized (map) {
            //即使一次只发射一次，会不会也太频繁了？？再慢一点？
            //只要看水平方向有没有敌人就行了，一边最多一个
            Bullet bullet = null;
            Creature enemy = null;
            if (bulletGenerator instanceof StraightBulletGenerator){
                //顺序判断四个方向哪一个有敌人
                enemy = searchStraightEnemy();
            }
            else if(bulletGenerator instanceof TrackBulletGenerator) {
                //追踪弹,攻击斜45度方向
                enemy = searchCorssEnemies();
            }
            if(enemy == null){//找到了敌人，这次要发射了
                lastTimeSent = false;//记录：上一次(这一次)没有发射
                return;
            }
            if (lastTimeSent && (!random.nextBoolean())) {
                lastTimeSent = false;//本次就不发射了
                return;
            }
            lastTimeSent = true;
            //确定要发射子弹了
            int x = position.getX();
            int y = position.getY();
            double bulletX = x * UNIT_SIZE + (UNIT_SIZE - BULLTE_RADIUS) / 2;
            double bulletY = y * UNIT_SIZE + (UNIT_SIZE - BULLTE_RADIUS) / 2;
            int damage = this.attackValue - enemy.defenseValue;
            if (damage <= 0) {
                damage = 10;
            }
            synchronized (enemy){//防止这时候enemy移动，其实没什么用
                bullet = bulletGenerator.getBullet(this, enemy, damage, bulletX, bulletY);
            }
            if (camp == Camp.JUSTICE) {
                bullet.setColor(Color.LIGHTGREEN);
            } else
                bullet.setColor(Color.DEEPPINK);
            synchronized (bullets) {//添加子弹
                bullets.add(bullet);
            }

        }
    }

    public void getHurt(int damage) {//受到攻击
        if (damage > currentHP) {
            currentHP = 0;
            alive = false;
            //TODO map要设置怎么画死亡的生物
//            System.out.println("dead");
        } else {
//            System.out.println("hurt");
            currentHP -= damage;
        }
    }

    public void setPosition(int x, int y) {
        this.position.setX(x);
        this.position.setY(y);
    }

    public void move() {//移动,只在生物的run线程中调用,已经取得了map的锁
        //随机生成两个x y 方向的0 1 -1 值
        int xStep = random.nextInt(3) - 1;//-1 0 1
        int yStep = random.nextInt(3) - 1;
        int oldX = position.getX();
        int oldY = position.getY();
        int newX = oldX + xStep;
        int newY = oldY + yStep;
        if (map.insideMap(newX, newY) && map.noCreatureAt(newX, newY)) {
            map.removeCreatureAt(oldX, oldY);
            map.setCreatureAt(newX, newY, this);//放置自己
            synchronized (this) {//锁住自己，这时候不能被攻击
                setPosition(newX, newY);
            }
        }
        //如果不符合条件，就不移动
    }

    public Position getPosition() {
        return position;
    }

    //移动生物并且设置map，确保无位置冲突
    public void moveTo(int newX, int newY) {
        //移动到(x,y)处，需要synchronized map
        synchronized (map) {//对map上锁
            synchronized (this) {
                if (map.insideMap(newX, newY) && map.noCreatureAt(newX, newY)) {
                    map.setCreatureAt(newX, newY, this);
                    setPosition(newX, newY);
                }
            }
        }
    }

    public abstract void resetState();//和构造函数对应

    public Image getImage() {
        return image;
    }

    Creature searchCorssEnemies() {
        Creature enemy = null;
        //寻找同一行的敌人
        int x = position.getX();
        int y = position.getY();
        //依次向四个方向寻找
        synchronized (map) {
            //左上
            int nextX = x - 1;
            int nextY = y - 1;
            while (map.insideMap(nextX, nextY)) {
                if ((map.noCreatureAt(nextX, nextY) == false) && (map.getCreatureAt(nextX, nextY).getCamp() != this.camp)
                        && (map.getCreatureAt(nextX, nextY).isAlive())) {
                    return map.getCreatureAt(nextX, nextY);
                }
                --nextX;
                --nextY;
            }
            //右上
            nextX = x - 1;
            nextY = y + 1;
            while (map.insideMap(nextX, nextY)) {
                if ((map.noCreatureAt(nextX, nextY) == false) && (map.getCreatureAt(nextX, nextY).getCamp() != this.camp)
                        && (map.getCreatureAt(nextX, nextY).isAlive())) {
                    return map.getCreatureAt(nextX, nextY);
                }
                --nextX;
                ++nextY;
            }

            //左下
            nextX = x + 1;
            nextY = y - 1;
            while (map.insideMap(nextX, nextY)) {
                if ((map.noCreatureAt(nextX, nextY) == false) && (map.getCreatureAt(nextX, nextY).getCamp() != this.camp)
                        && (map.getCreatureAt(nextX, nextY).isAlive())) {
                    return map.getCreatureAt(nextX, nextY);
                }
                ++nextX;
                --nextY;
            }

            //右下
            nextX = x + 1;
            nextY = y + 1;
            while (map.insideMap(nextX, nextY)) {
                if ((map.noCreatureAt(nextX, nextY) == false) && (map.getCreatureAt(nextX, nextY).getCamp() != this.camp)
                        && (map.getCreatureAt(nextX, nextY).isAlive())) {
                    return map.getCreatureAt(nextX, nextY);
                }
                ++nextX;
                ++nextY;
            }

            return null;
        }
    }

    ArrayList<Creature> searchSudokuFriends() {
        ArrayList<Creature> friends = new ArrayList<>();
        //寻找九宫格内的队友
        synchronized (map) {
            int x = position.getX() - 1;
            int y = position.getY() - 1;
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 3; ++j) {
                    int newX = x + i;
                    int newY = y + j;
                    if (map.insideMap(newX, newY)) {
                        //还是没有同步
                        Creature c = map.getCreatureAt(newX, newY);
                        if (c != null && c.camp == this.camp && c.alive && (!(i == 1 && j == 1))) {//不能治愈自己
                            friends.add(c);
                        }
                    }
                }
            }
        }
        return friends;
    }

    /**
     * @return 直线方向上的某一个敌人
     */
    Creature searchStraightEnemy(){
          int x = position.getX();
        int y = position.getY();
        synchronized (map){
            for(int i = 0;i < NUM_COLUMNS;++i){
                if((i != y)&&(!map.noCreatureAt(x,i))
                        &&(map.getCreatureAt(x, i).getCamp() != this.camp)
                        &&map.getCreatureAt(x,i).isAlive()){
                    return map.getCreatureAt(x,i);
                }
            }
            for(int i = 0;i < NUM_ROWS;++i){
                if((i != x)&&(!map.noCreatureAt(i,y))
                        &&(map.getCreatureAt(i, y).getCamp() != this.camp)
                        &&map.getCreatureAt(i,y).isAlive()){
                    return map.getCreatureAt(i,y); }
            }
        }
        return null;//找不到生物
    }

    public Camp getCamp() {
        return camp;
    }

    @Override
    public void run() {//生物的run方法都差不多
        //进入循环后，黑莓移动，被杀死，ui显示一次，移动后，再显示一次，体现为墓碑移动
        //因此在移动时要上锁(攻击无所谓，发出子弹就一瞬间的事)
        while (alive && Thread.interrupted() == false) {//死亡或者pool调用了shutDownNow则本线程退出
            //如果暂停的话，BattleField只要把所以生物的alive置为false,就能结束所有生物线程并且生物状态不变了
            try {
                Thread.sleep(1000 / moveRate);
                if (alive == false)
                    break;//sleep后发现自己死了
                synchronized (map) {//上锁顺序 map -> creature(this)
                    attack();//attack()对map、enemy、bullet上锁
                    move();//move方法内部已经对map上锁了
                }
            } catch (InterruptedException e) {
                break;//在sleep的时候shutDownNow结束线程
            }
        }
//        System.out.println("Creature.run() exit");

    }

    public boolean isAlive() {
        return alive;
    }

    public int getCurrentHP() {
        return currentHP;
    }

    public int getMAX_HP() {
        return MAX_HP;
    }

    public void heal(int blood) {
        //回血
        if (alive) {
            currentHP += blood;
            if (currentHP > MAX_HP) {
                currentHP = MAX_HP;
            }
        }
    }

    public String getSimpleName() {
        return "Creature";
    }
}
