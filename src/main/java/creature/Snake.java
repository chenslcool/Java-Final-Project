package creature;

import battle.Map;
import bullet.Bullet;
import bullet.HorizontalBulletFactory;
import bullet.TrackBulletFactory;
import creature.enumeration.Camp;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.LinkedList;

/**
 * @author csl
 * @date 2019/11/25 16:50
 */
public class Snake extends Creature {
    public Snake(Map map, LinkedList<Bullet> bullets){
        super(map,bullets);
        URL url = this.getClass().getClassLoader().getResource("pictures/" + "snake.jpg");
        this.image = new Image(url.toString());
        this.camp = Camp.EVIL;
        bulletBulletFactory = new TrackBulletFactory();
    }
//    @Override
//    public void attack() {
//
//    }

    //TODO 蛇精可以有CheerUp方法，添加友方攻击力
}
