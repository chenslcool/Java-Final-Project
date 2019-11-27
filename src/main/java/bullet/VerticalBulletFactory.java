package bullet;

import battle.Map;
import creature.Creature;

/**
 * @author csl
 * @date 2019/11/27 7:59
 */
public class VerticalBulletFactory extends BulletFactory<Bullet> {

    @Override
    public VerticalBullet getBullet(Map map, Creature sender, Creature target,int damage, double x, double y) {
        return new VerticalBullet(map,sender,target,damage,x,y);
    }
}