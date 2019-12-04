package bullet;

import battle.Map;
import creature.Creature;
import javafx.scene.paint.Color;

/**
 * @author csl
 * @date 2019/11/25 19:05
 */
public class HorizontalBullet extends Bullet {
    private boolean toRight;//飞行方向

    public HorizontalBullet(Map map, Creature sender, Creature target, int damage, double x, double y) {
        super(map, sender, target, damage, x, y);
        this.toRight = target.getPosition().getY() > sender.getPosition().getY();
    }

    @Override
    public void move() {
        //向右移动
        //newY，判断(x,newY)是否为有效坐标
        //设置新位置
        if (toRight) {
            this.y += STEP_DISTANCE;
        } else {
            this.y -= STEP_DISTANCE;
        }
    }

    @Override
    public String toString() {
        return "Horizontal Bullet: start from(" + x + "," + y + ")," + toRight;
    }
}
