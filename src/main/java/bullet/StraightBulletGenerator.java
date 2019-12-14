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
    public StraightBullet getBullet(Creature sender, Creature target, int damage, double x, double y) throws Exception {
        if(sender == null){
            throw new Exception("sender cannot be null");
        }
        return new StraightBullet(sender, target, damage, x, y);
    }
}
