package bullet;

import creature.Creature;
import creature.enumeration.Direction;

/**
 * @author csl
 * @date 2019/11/25 19:05
 */
public class StraightBullet extends Bullet {
    private Direction direction;

    /**
     * @param sender sender of bullet
     * @param target target of bullet,if null,must setDirection using setDirection()
     * @param damage damage of bullet
     * @param x X position
     * @param y Y position
     */
    public StraightBullet(Creature sender, Creature target, int damage, double x, double y) {
        super(sender, target, damage, x, y);
        if(target != null)//否则setToright由Grandpa确定
        {
            //按照右左上下的顺序判断
            if(target.getPosition().getY() > sender.getPosition().getY()){
                this.direction = Direction.RIGHT;
            }
            else if(target.getPosition().getY() < sender.getPosition().getY()){
                this.direction = Direction.LEFT;
            }
            else if(target.getPosition().getX() > sender.getPosition().getX()){
                this.direction = Direction.DOWN;
            }
            else{
                this.direction = Direction.UP;
            }
        }
    }


    /**
     * @param direction direction of bullet
     * if target in constructor is null,must set direction using this method
     */
    public void setDirection(Direction direction){
        this.direction = direction;
    }

    @Override
    public void move() {
        switch (this.direction){
            case RIGHT:{
                this.y += STEP_DISTANCE;
            }break;
            case LEFT:{
                this.y -= STEP_DISTANCE;
            }break;
            case UP:{
                this.x -= STEP_DISTANCE;
            }break;
            case DOWN:{
                this.x += STEP_DISTANCE;
            }
        }
    }

    @Override
    public String toString() {
        return "Horizontal Bullet: start from(" + x + "," + y + ")," + direction;
    }
}
