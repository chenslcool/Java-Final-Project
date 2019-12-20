package creature;

import annotations.Info;
import battle.Map;
import bullet.*;
import creature.enumeration.Camp;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author csl
 * @date 2019/11/27 19:16
 */
@Info(description = "GrandPa,player can control it,so has two queue to denote player-commands.")
public class GrandPa extends JusticeCreature implements Curable {
    private static String simpleName = "GrandPa";
//    private long lastTimeSendBullet = System.currentTimeMillis();
    public GrandPa(Map map, Image image, String name, LinkedList<Bullet> bullets) {
        super(map, bullets);
        this.image = image;
        this.name = name;
        this.camp = Camp.JUSTICE;
        this.attackValue = GRANDPA_ATK;
        this.defenseValue = GRANDPA_DEF;

        //diff
        this.moveRate = DEFAULT_MOVE_RATE;

        bulletGenerator = new StraightBulletGenerator();//GrandPa只能在控制下发射直行的子弹
    }

    @Override
    public void attack() {
        //老爷爷的attack(这个方法名不好，应该改成act)就是移动，治愈队友
        //治愈九宫格之内的
        synchronized (map) {
            cure();
        }
        super.attack();//JusticeCreature的attack()
    }

    @Override
    public void cure() {
        synchronized (map) {
            ArrayList<Creature> friends = searchSudokuFriends();
            for (Creature c : friends) {
                c.heal(HEAL_BLOOD);
            }
        }
    }

    @Override
    public String getSimpleName() {
        return "GrandPa";
    }

    @Override
    public void resetState() {
        this.MAX_HP = DEFAULT_MAX_HP;
        this.currentHP = this.MAX_HP;
        this.attackValue = GRANDPA_ATK;
        this.defenseValue = GRANDPA_DEF;
        this.alive = true;
//        this.moveRate = DEFAULT_MOVE_RATE;
    }

}
