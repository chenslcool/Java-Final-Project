package creature;

import annotations.Info;
import battle.Map;
import bullet.Bullet;
import bullet.StraightBulletGenerator;
import creature.enumeration.Camp;
import javafx.scene.image.Image;

import java.util.LinkedList;

/**
 * @author csl
 * @date 2019/11/24 21:26
 */
@Info(description = "huluwa")
public class Huluwa extends JusticeCreature {
    private static String simpleName = "Huluwa";
    private int rank;

    public Huluwa(Map map, Image image, String name, int rank, LinkedList<Bullet> bullets) {
        super(map, bullets);
        this.image = image;
        this.name = name;
        this.camp = Camp.JUSTICE;
        this.attackValue = HULUWA_ATK;
        this.defenseValue = HULUWA_DEF;
        this.rank = rank;
        bulletGenerator = new StraightBulletGenerator();
    }

    @Override
    public String getSimpleName() {
        return "Huluwa" + rank;
    }//用于回放

    @Override
    public void resetState() {
        this.MAX_HP = DEFAULT_MAX_HP;
        this.currentHP = this.MAX_HP;
        this.attackValue = HULUWA_ATK;
        this.defenseValue = HULUWA_DEF;
        this.alive = true;
        this.moveRate = DEFAULT_MOVE_RATE;
    }

}
