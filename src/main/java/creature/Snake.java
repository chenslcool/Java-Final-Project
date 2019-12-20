package creature;

import annotations.Info;
import battle.Map;
import bullet.Bullet;
import bullet.TrackBulletGenerator;
import creature.enumeration.Camp;
import creature.enumeration.Direction;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author csl
 * @date 2019/11/25 16:50
 */
@Info(description = "Snake,similar to scorpion but curable")
public class Snake extends EvilCreature implements Curable {
    private static String simpleName = "Snake";
    public Snake(Map map, LinkedList<Bullet> bullets) {
        super(map, bullets);
        URL url = this.getClass().getClassLoader().getResource("pictures/" + "snake.png");
        this.image = new Image(url.toString());
        this.camp = Camp.EVIL;
        this.attackValue = EVIL_LEADER_ATK;
        this.defenseValue = EVIL_LEADER_DEF;
        bulletGenerator = new TrackBulletGenerator();
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
        return "Snake";
    }

    @Override
    public void resetState() {
        this.MAX_HP = DEFAULT_MAX_HP;
        this.currentHP = this.MAX_HP;
        this.attackValue = EVIL_LEADER_ATK - 10;
        this.defenseValue = EVIL_LEADER_DEF;
        this.alive = true;
    }

    @Override
    public void attack() {
        //治愈九宫格之内的
        synchronized (map) {
            cure();
        }
        super.attack();
    }
}
