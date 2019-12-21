package bullet;

import annotations.Info;
import battle.Config;
import battle.Map;
import creature.Creature;
import creature.Position;
import javafx.scene.paint.Color;


/**
 * @author csl
 * @date 2019/11/25 18:56
 */

@Info(description = "use bullet to attack,field:position(x,y),damage,color,target and sender")
public abstract class Bullet implements Config {
    //子弹只有位置和颜色需要保存
    double x;//左上角的坐标,是以像素为单位
    double y;
//    protected Color color;//子弹的颜色
    protected transient int damage;//伤害
    protected transient Creature sender;//发射者，记录的一个原因是不能攻击自己
    protected transient Creature target;

    //至于目标是谁，在基类Bullet里面并不重要，因为不同的子弹的移动方式也是不一样的(直线、追踪)
    public Bullet(Creature sender, Creature target, int damage, double x, double y) {
        this.sender = sender;
        this.damage = damage;
        this.x = x;
        this.y = y;
        this.target = target;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

//    public Color getColor() {
//        return color;
//    }

    public int getDamage() {
        return damage;
    }

    public Creature getSender() {
        return sender;
    }

    public abstract void move() throws Exception;

    public boolean outOfMap() {
        return (this.x < 0.1 || this.y < 0.1 || this.x >= CANVAS_HEIGHT - 0.1 || this.y >= CANVAS_WIDTH - 0.1);
    }

//    public void setColor(Color color) {
//        this.color = color;
//    }

    public Creature getTarget() {
        return target;
    }

    public int getCountDown() {
        return TRACK_COUNT_DOWN;
    }
}
