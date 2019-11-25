package creature;

import battle.Map;
import bullet.Bullet;
import bullet.HorizontalBullet;
import creature.enumeration.Camp;
import javafx.scene.image.Image;

import java.util.ArrayList;
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
    }
    public void attack() {
        //TODO search the map and found Enimies and send bullets
        //首先寻找九宫格之内的敌人
        //算了，先发射水平子弹吧
        //寻找水平方向的敌人
        synchronized (map){
            ArrayList<Creature> enemies = searchLineEnemies();
            for(Creature enemy : enemies){
                boolean toRight = enemy.position.getY() > this.position.getY();
                Bullet bullet = new HorizontalBullet(map,this,this.attackValue,toRight,position.getX(),position.getY());
                synchronized (bullets){//添加子弹
                    bullets.add(bullet);
                }
//                System.out.println("add bullet:"+bullet);
            }
        }

    }

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
