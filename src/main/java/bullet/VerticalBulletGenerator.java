package bullet;

import battle.Map;
import creature.Creature;

/**
 * @author csl
 * @date 2019/11/27 7:59
 */
public class VerticalBulletGenerator extends BulletGenerator<Bullet> {

    @Override
    public VerticalBullet getBullet(Creature sender, Creature target, int damage, double x, double y) {
        return new VerticalBullet(sender, target, damage, x, y);
    }
}