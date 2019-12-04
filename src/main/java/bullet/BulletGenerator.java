package bullet;

import battle.Map;
import creature.Creature;

/**
 * @author csl
 * @date 2019/11/27 7:54
 * generate a bullet (factory mode)
 */
public abstract class BulletGenerator<T> {
    public abstract T getBullet(Map map, Creature sender, Creature target, int damage, double x, double y);
}
