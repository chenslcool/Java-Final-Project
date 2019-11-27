package creature;

import battle.Map;
import bullet.Bullet;
import bullet.HorizontalBulletFactory;
import bullet.VerticalBulletFactory;
import creature.enumeration.Camp;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.LinkedList;

/**
 * @author csl
 * @date 2019/11/25 16:51
 */
public class Scorpion extends Creature {
    public Scorpion(Map map, LinkedList<Bullet> bullets){
        super(map,bullets);
        URL url = this.getClass().getClassLoader().getResource("pictures/" + "scorpion.jpg");
        this.image = new Image(url.toString());
        this.camp = Camp.EVIL;
        bulletBulletFactory = new HorizontalBulletFactory();
    }
//    @Override
//    public void attack() {
//
//    }
    //TODO 给蝎子精添加特殊技能
}
