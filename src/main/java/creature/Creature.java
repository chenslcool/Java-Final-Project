package creature;

import battle.Config;
import battle.Map;
import bullet.Bullet;
import creature.enumeration.Camp;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
 * @author csl
 * @date 2019/11/24 20:49
 */
public abstract class Creature implements Runnable, Config {
    protected Camp camp;
    protected Random random;
    protected boolean alive;
    protected Position position;
    protected Map map;
    protected Image image;
    protected String name;
    protected int MAX_HP;
    protected int currentHP;
    protected int attackValue;//攻击力 > 50
    protected int defenseValue;//防御力 < 50
    protected int moveRate;//速度,sleepTime = 1000ms/moveRate;
    protected LinkedList<Bullet> bullets;
    public Creature(){}
    public Creature(Map map,LinkedList<Bullet> bullets){
        camp = Camp.JUSTICE;
        random = new Random();
        alive = true;
        MAX_HP = DEFAULT_MAX_HP;
        currentHP = MAX_HP;
        attackValue = DEFAULT_ATTACK_VALUE;
        defenseValue = DEFAULT_DEFENSE_VALUE;
        moveRate = DEFAULT_MOVE_RATE;//0.5 s
        this.map = map;
        position = new Position();
        this.bullets = bullets;
    }
    public abstract void attack();//不同的生物有不同的攻击方式：近距离 or 子弹
    public void getHurt(int damage){//受到攻击
        if(damage > currentHP){
            currentHP = 0;
            alive = false;
        }
        else{
            currentHP -= damage;
        }
    }
    public void setPosition(int x,int y){
        this.position.setX(x);
        this.position.setY(y);
    }
    public void move(){//移动
        //随机生成两个x y 方向的0 1 -1 值
        int xStep = random.nextInt(3) -1;//-1 0 1
        int yStep = random.nextInt(3) -1;
        int oldX = position.getX();
        int oldY = position.getY();
        int newX = oldX + xStep;
        int newY = oldY + yStep;
        synchronized (map){//对map上锁
            if(map.insideMap(newX,newY) && map.noCreatureAt(newX,newY)){
                map.removeCreatureAt(oldX,oldY);
                map.setCreatureAt(newX,newY,this);//放置自己
                setPosition(newX,newY);
            }
        }
        //如果不符合条件，就不移动
    }

    public Position getPosition(){
        return position;
    }
    //移动生物并且设置map，确保无位置冲突
    public void moveTo(int newX,int newY){
        //移动到(x,y)处，需要synchronized map
        synchronized (map){//对map上锁
            if(map.insideMap(newX,newY) && map.noCreatureAt(newX,newY)){
                map.setCreatureAt(newX,newY,this);
            }
            setPosition(newX,newY);
        }
    }

    public void resetState(){
        //恢复正常状态
        MAX_HP = DEFAULT_MAX_HP;
        currentHP = MAX_HP;
        attackValue = DEFAULT_ATTACK_VALUE;
        defenseValue = DEFAULT_DEFENSE_VALUE;
        moveRate = DEFAULT_MOVE_RATE;//0.5 s
        alive = true;
    }

    public Image getImage(){
        return image;
    }

    ArrayList<Creature> searchLineEnemies(){
        //寻找同一行的敌人
        ArrayList<Creature> enemies = new ArrayList<>();
        int x = position.getX();
        int y = position.getY();
        synchronized (map){
            for(int i = 0;i<NUM_COLUMNS;++i){
                //这个地方有地方生物
                if((i != y) && (map.noCreatureAt(x,i) == false) && (map.getCreatureAt(x,i).getCamp() != this.camp))
                    enemies.add(map.getCreatureAt(x,i));
            }
            return enemies;
        }
    }
    public Camp getCamp(){
        return camp;
    }
    @Override
    public void run() {//生物的run方法都差不多
        while(alive){
            //如果暂停的话，BattleField只要把所以生物的alive置为false,就能结束所有生物线程并且生物状态不变了
            try {
                Thread.sleep(1000/moveRate);
                attack();
                move();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
