package bullet;

import annotations.Info;
import battle.Map;
import creature.Creature;

/**
 * @author csl
 * @date 2019/11/27 7:54
 * generate a bullet (factory mode)
 */
@Info(description = "generate a bullet")
public abstract class BulletGenerator<T extends Bullet> {
    public abstract T getBullet(Creature sender, Creature target, int damage, double x, double y);
}
