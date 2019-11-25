package bullet;

import battle.Map;
import creature.Creature;
import javafx.scene.paint.Color;

/**
 * @author csl
 * @date 2019/11/25 19:05
 */
//水平飞的子弹
public class HorizontalBullet extends Bullet {
    private boolean toRight;//飞行方向
    public HorizontalBullet(Map map, Creature sender, int damage,boolean toRight,int x,int y){
        super(map,sender,damage,x,y);
        this.toRight = toRight;
        this.color = Color.RED;
    }

    @Override
    public void move() {
        //向右移动
        //newY，判断(x,newY)是否为有效坐标
        //设置新位置
        double newY = this.y + STEP_DISTANCE;//对于水平子弹，x坐标是不会变的
        this.y = newY;//是否出界由bulletManager判断
    }
    @Override
    public String toString(){
        return "Horizontal Bullet: start from("+x+","+y+"),"+toRight;
    }
}
