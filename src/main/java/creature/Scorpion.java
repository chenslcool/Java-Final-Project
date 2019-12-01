package creature;

import battle.Map;
import bullet.Bullet;
import bullet.TrackBulletGenerator;
import creature.enumeration.Camp;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.LinkedList;

/**
 * @author csl
 * @date 2019/11/25 16:51
 */
public class Scorpion extends Creature {
    private static String simpleName = "Scorpion";
    public Scorpion(Map map, LinkedList<Bullet> bullets){
        super(map,bullets);
        URL url = this.getClass().getClassLoader().getResource("pictures/" + "scorpion.png");
        this.image = new Image(url.toString());
        this.camp = Camp.EVIL;
        this.attackValue = EVIL_LEADER_ATK;
        this.defenseValue = EVIL_LEADER_DEF;
        bulletBulletGenerator = new TrackBulletGenerator();
    }
    @Override
    public String getSimpleName(){
        return "Scorpion";
    }
}
