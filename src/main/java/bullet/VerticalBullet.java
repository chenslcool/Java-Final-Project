package bullet;

import battle.Map;
import creature.Creature;
import javafx.scene.paint.Color;

/**
 * @author csl
 * @date 2019/11/27 7:51
 */
//垂直飞行的子弹
public class VerticalBullet extends Bullet{
    private boolean toUp;//飞行方向
    public VerticalBullet(Map map, Creature sender,Creature target, int damage, double x, double y){
        super(map,sender,target,damage,x,y);
        this.toUp = target.getPosition().getX() < sender.getPosition().getX();
//        this.color = Color.RED;
    }

    @Override
    public void move() {
        //newY，判断(x,newY)是否为有效坐标
        //设置新位置
        if(toUp){
            this.x -=  STEP_DISTANCE;
        }
        else{
            this.y +=  STEP_DISTANCE;
        }
    }
    @Override
    public String toString(){
        return "Vertical Bullet: start from("+x+","+y+"),"+toUp;
    }
}
