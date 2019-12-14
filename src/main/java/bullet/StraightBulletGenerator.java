package bullet;

import annotations.Info;
import creature.Creature;

/**
 * @author csl
 * @date 2019/11/27 7:56
 */
@Info(description = "generate straight bullet")
public class StraightBulletGenerator extends BulletGenerator<StraightBullet> {

    @Override
    public StraightBullet getBullet(Creature sender, Creature target, int damage, double x, double y) {
        return new StraightBullet(sender, target, damage, x, y);
    }
}
