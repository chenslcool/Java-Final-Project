package bullet;

import battle.Config;
import battle.Map;
import creature.Creature;
import creature.Position;
import javafx.scene.paint.Color;


/**
 * @author csl
 * @date 2019/11/25 18:56
 */
public abstract class Bullet implements Config {
    double x;//左上角
    double y;
    protected Map map;//也要知道自己的位置
    protected int damage;//伤害
    protected Color color;//子弹的颜色
    protected Creature sender;//发射者，记录的一个原因是不能攻击自己
    //至于目标是谁，在基类Bullet里面并不重要，因为不同的子弹的移动方式也是不一样的(直线、追踪)
    public Bullet (Map map,Creature sender,int damage,double x,double y){
        this.map = map;
        this.sender = sender;
        this.damage = damage;
        this.x = x;
        this.y = y;
    }
    public double getX(){
        return x;
    }

    public double getY() {
        return y;
    }

    public Color getColor() {
        return color;
    }

    public int getDamage(){
        return damage;
    }

    public Creature getSender(){
        return sender;
    }

    public abstract void move();

    public boolean outOfMap(){
        return (this.x < 0 || this.y < 0 || this.x  >= NUM_ROWS|| this.y >= NUM_COLUMNS);
    }
}
