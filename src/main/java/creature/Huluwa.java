package creature;

import battle.Map;
import bullet.Bullet;
import bullet.HorizontalBulletGenerator;
import creature.enumeration.Camp;
import javafx.scene.image.Image;

import java.util.LinkedList;

/**
 * @author csl
 * @date 2019/11/24 21:26
 */
public class Huluwa extends Creature {
    public Huluwa(Map map, Image image, String name, LinkedList<Bullet> bullets){
        super(map,bullets);
        this.image = image;
        this.name = name;
        this.camp = Camp.JUSTICE;
        this.attackValue = HULUWA_ATK;
        this.defenseValue = HULUWA_DEF;
        bulletBulletGenerator = new HorizontalBulletGenerator();
    }
//    public void attack() {
//
//
//    }

    //run() 是用的从Creature继承的
//    public void run() {
//        while(alive){
//            //如果暂停的话，BattleField只要把所以生物的alive置为false,就能结束所有生物线程并且生物状态不变了
//            try {
//                Thread.sleep(1000/moveRate);
//                attack();
//                move();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
