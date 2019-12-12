package bullet;

import battle.Map;
import creature.Creature;

/**
 * @author csl
 * @date 2019/11/27 12:48
 */
public class TrackBulletGenerator extends BulletGenerator<TrackBullet> {
    @Override
    public TrackBullet getBullet(Creature sender, Creature target, int damage, double x, double y) {
        return new TrackBullet(sender, target, damage, x, y);
    }
}
