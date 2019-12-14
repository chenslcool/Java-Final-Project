import bullet.*;
import creature.Creature;
import creature.Huluwa;
import creature.enumeration.Direction;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author csl
 * @date 2019/12/14 14:45
 */
public class MyTest {
    @Test
    public void testBulletMove(){
        BulletGenerator<StraightBullet> bulletBulletGenerator = new StraightBulletGenerator();
        StraightBullet bullet = null;
        try {
            bullet = bulletBulletGenerator.getBullet(new Creature() {
                @Override
                public void resetState() {

                }
            }, null, 10, 100, 100);
        } catch (Exception e) {
            e.printStackTrace();
        }
        bullet.setDirection(Direction.UP);
        bullet.move();
        assertEquals((int)bullet.getX(),(int)85);
        assertEquals((int)bullet.getY(),(int)100);
        bullet.move();
        assertEquals((int)bullet.getX(),(int)70);
        assertEquals((int)bullet.getY(),(int)100);
    }
    @Test(expected = Exception.class)
    public void testBulletSenderNotNull() throws Exception {
        BulletGenerator<StraightBullet> bulletBulletGenerator = new StraightBulletGenerator();
        bulletBulletGenerator.getBullet(null,null,10,100,100);
        fail("sender 为null，getBullet没有抛出异常");
    }
    @Test(expected = Exception.class)
    public void textTrackBulletTargetNotNull() throws Exception {
        BulletGenerator<TrackBullet> bulletBulletGenerator = new TrackBulletGenerator();
        bulletBulletGenerator.getBullet(new Creature() {
            @Override
            public void resetState() {

            }
        },null,10,10,10);
        fail("target 为null，getBullet没有抛出异常");
    }
}
