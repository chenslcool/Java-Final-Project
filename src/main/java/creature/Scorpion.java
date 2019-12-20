package creature;

import annotations.Info;
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
@Info(description = "Scorpion,stronger than common evil")
public class Scorpion extends Creature {
    private static String simpleName = "Scorpion";

    public Scorpion(Map map, LinkedList<Bullet> bullets) {
        super(map, bullets);
        URL url = this.getClass().getClassLoader().getResource("pictures/" + "scorpion.png");
        this.image = new Image(url.toString());
        this.camp = Camp.EVIL;
        this.attackValue = EVIL_LEADER_ATK;
        this.defenseValue = EVIL_LEADER_DEF;
        bulletGenerator = new TrackBulletGenerator();
    }

    @Override
    public String getSimpleName() {
        return "Scorpion";
    }

    @Override
    public void resetState() {
        this.MAX_HP = DEFAULT_MAX_HP;
        this.currentHP = this.MAX_HP;
        this.attackValue = EVIL_LEADER_ATK;
        this.defenseValue = EVIL_LEADER_DEF;
        this.alive = true;
//        this.moveRate = DEFAULT_MOVE_RATE;
    }
}
