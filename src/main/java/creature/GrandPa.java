package creature;

import battle.Map;
import bullet.Bullet;
import bullet.HorizontalBulletFactory;
import creature.enumeration.Camp;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author csl
 * @date 2019/11/27 19:16
 */
public class GrandPa extends Creature implements Curable {
    public GrandPa(Map map, Image image, String name, LinkedList<Bullet> bullets){
        super(map,bullets);
        this.image = image;
        this.name = name;
        this.camp = Camp.JUSTICE;
//        bulletBulletFactory = new HorizontalBulletFactory();
    }
    @Override
    public void attack(){
        //老爷爷的attack(这个方法名不好，应该改成act)就是移动，治愈队友
        //治愈九宫格之内的
        synchronized (map){
            cure();
        }
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
}
