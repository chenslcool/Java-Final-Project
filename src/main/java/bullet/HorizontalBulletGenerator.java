package bullet;

import battle.Map;
import creature.Creature;

/**
 * @author csl
 * @date 2019/11/27 7:56
 */
public class HorizontalBulletGenerator extends BulletGenerator<Bullet> {

    @Override
    public HorizontalBullet getBullet(Creature sender, Creature target, int damage, double x, double y) {
        return new HorizontalBullet(sender, target, damage, x, y);
    }
}
