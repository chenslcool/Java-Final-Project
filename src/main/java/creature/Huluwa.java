package creature;

import battle.Map;
import javafx.scene.image.Image;

/**
 * @author csl
 * @date 2019/11/24 21:26
 */
public class Huluwa extends Creature {
    public Huluwa(Map map, Image image,String name){
        super(map);
        this.image = image;
        this.name = name;
    }
    public void attack() {
        //TODO search the map and found Enimies and send bullets
    }

    public void run() {
        while(alive){
            //如果暂停的话，BattleField只要把所以生物的alive置为false,就能结束所有生物线程并且生物状态不变了
            try {
                Thread.sleep(1000/moveRate);
                attack();
                move();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
