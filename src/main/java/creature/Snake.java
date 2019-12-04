package creature;

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
public class Snake extends Creature implements Curable {
    private boolean control = false;
    private static String simpleName = "Snake";
    private transient LinkedList<Direction> directions = new LinkedList<Direction>();

    public Snake(Map map, LinkedList<Bullet> bullets) {
        super(map, bullets);
        URL url = this.getClass().getClassLoader().getResource("pictures/" + "snake.png");
        this.image = new Image(url.toString());
        this.camp = Camp.EVIL;
        this.attackValue = EVIL_LEADER_ATK;
        this.defenseValue = EVIL_LEADER_DEF;
        bulletBulletGenerator = new TrackBulletGenerator();
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
        this.moveRate = DEFAULT_MOVE_RATE * 2;
    }

    public void addDirection(Direction direction) {
        synchronized (directions) {
            directions.add(direction);
        }
    }

    public void clearDirection() {
        synchronized (directions) {
            directions.clear();
        }
    }

    @Override
    public void move() {
        if (control == false) {
            super.move();
        }
        //查看方向queue
        synchronized (directions) {
            if (directions.isEmpty() == false) {
                Direction curDirection = directions.pollFirst();//得到第一个
                int newX = position.getX();
                int newY = position.getY();
                switch (curDirection) {
                    case UP: {
                        newX -= 1;
                    }
                    break;
                    case DOWN: {
                        newX += 1;
                    }
                    break;
                    case RIGHT: {
                        newY += 1;
                    }
                    break;
                    default: {
                        newY -= 1;
                    }
                }
                if (map.insideMap(newX, newY) && map.noCreatureAt(newX, newY)) {
                    map.removeCreatureAt(position.getX(), position.getY());
                    map.setCreatureAt(newX, newY, this);//放置自己
                    synchronized (this) {//锁住自己
                        setPosition(newX, newY);
                    }
//                    System.out.println("move");
//                    return;//按键方向成功
                }
            }
        }
//        super.move();//不成功就用之前的move
    }

    @Override
    public void attack() {
        if (control == false) {
            super.attack();
        }
        //老爷爷的attack(这个方法名不好，应该改成act)就是移动，治愈队友
        //治愈九宫格之内的
        synchronized (map) {
            cure();
        }
        super.attack();
    }
}
