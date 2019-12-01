package creature;

import battle.Map;
import bullet.Bullet;
import bullet.TrackBulletGenerator;
import creature.enumeration.Camp;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author csl
 * @date 2019/11/25 16:50
 */
public class Snake extends Creature implements Curable{
    private static String simpleName = "Snake";
    public Snake(Map map, LinkedList<Bullet> bullets){
        super(map,bullets);
        URL url = this.getClass().getClassLoader().getResource("pictures/" + "snake.png");
        this.image = new Image(url.toString());
        this.camp = Camp.EVIL;
        this.attackValue = EVIL_LEADER_ATK;
        this.defenseValue = EVIL_LEADER_DEF;
        bulletBulletGenerator = new TrackBulletGenerator();
    }

    @Override
    public void cure() {
        synchronized (map){
            ArrayList<Creature> friends = searchSudokuFriends();
            for(Creature c:friends){
                c.heal(HEAL_BLOOD);
            }
        }
    }
    @Override
    public String getSimpleName(){
        return "Snake";
    }
}
