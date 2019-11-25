package creature;

import battle.Map;
import javafx.scene.image.Image;

import java.util.Random;

/**
 * @author csl
 * @date 2019/11/24 20:49
 */
public abstract class Creature implements Runnable{
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
    public Creature(Map map){
        random = new Random();
        alive = true;
        MAX_HP = 100;
        currentHP = 100;
        attackValue = 60;
        defenseValue = 40;
        moveRate = 2;//0.5 s
        this.map = map;
        position = new Position();
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

    public void moveTo(int newX,int newY){
        //移动到(x,y)处，需要synchronized map
        synchronized (map){//对map上锁
            if(map.insideMap(newX,newY) && map.noCreatureAt(newX,newY)){
                map.setCreatureAt(newX,newY,this);
            }
            setPosition(newX,newY);
        }
    }

    public Image getImage(){
        return image;
    }

}
