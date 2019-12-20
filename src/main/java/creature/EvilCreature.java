package creature;

import battle.Map;
import bullet.Bullet;

import java.util.LinkedList;

/**
 * @author csl
 * @date 2019/12/20 15:45
 */
public abstract class EvilCreature extends Creature {
    public EvilCreature(Map map, LinkedList<Bullet> bullets){
        super(map,bullets);
    }
}
