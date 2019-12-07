package creature;

import battle.Map;
import bullet.*;
import creature.enumeration.Camp;
import creature.enumeration.Direction;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author csl
 * @date 2019/11/27 19:16
 */
public class GrandPa extends Creature implements Curable {
    private static String simpleName = "GrandPa";
    private LinkedList<Direction> moveDirections = new LinkedList<Direction>();
    private BulletGenerator<Bullet> horizonBulletGenerator;
    private BulletGenerator<Bullet> verticalBulletGenerator;
//    private Integer attackCount = 0;//玩家按下的攻击键数量
    private LinkedList<Direction> bulletDirection = new LinkedList<>();
    private long lastTimeSendBullet = System.currentTimeMillis();
    public GrandPa(Map map, Image image, String name, LinkedList<Bullet> bullets) {
        super(map, bullets);
        this.image = image;
        this.name = name;
        this.camp = Camp.JUSTICE;
        this.attackValue = GRANDPA_ATK;
        this.defenseValue = GRANDPA_DEF;

        //diff
        this.moveRate = DEFAULT_MOVE_RATE * 2;//为了快速相应方向键

        bulletBulletGenerator = new TrackBulletGenerator();
        horizonBulletGenerator = new HorizontalBulletGenerator();
        verticalBulletGenerator = new VerticalBulletGenerator();
    }

    @Override
    public void attack() {
        //老爷爷的attack(这个方法名不好，应该改成act)就是移动，治愈队友
        //治愈九宫格之内的
        synchronized (map) {
            cure();
        }
        //如果玩家按下攻击键a,就向水平方向发射两枚子弹,目前完成的比较粗糙
        synchronized (bulletDirection){
            if(bulletDirection.isEmpty() == false){
                Direction direction = bulletDirection.pollLast();
                int x = position.getX();
                int y = position.getY();
                double bulletX = x * UNIT_SIZE + (UNIT_SIZE - BULLTE_RADIUS) / 2;
                double bulletY = y * UNIT_SIZE + (UNIT_SIZE - BULLTE_RADIUS) / 2;
                Bullet bullet = null;
                switch (direction){
                    case UP:{
                        bullet = verticalBulletGenerator.getBullet(this,null,GRANDPA_ATK - GRANDPA_DEF,bulletX,bulletY);
                        VerticalBullet verticalBullet = (VerticalBullet) bullet;
                        verticalBullet.setToUp(true);
                    }break;
                    case DOWN:{
                        bullet = verticalBulletGenerator.getBullet(this,null,GRANDPA_ATK - GRANDPA_DEF,bulletX,bulletY);
                        VerticalBullet verticalBullet = (VerticalBullet) bullet;
                        verticalBullet.setToUp(false);
                    }break;
                    case LEFT:{
                        bullet = horizonBulletGenerator.getBullet(this,null,GRANDPA_ATK - GRANDPA_DEF,bulletX,bulletY);
                        HorizontalBullet horizontalBullet = (HorizontalBullet) bullet;
                        horizontalBullet.setToRight(false);
                    }break;
                    case RIGHT:{
                        bullet = horizonBulletGenerator.getBullet(this,null,GRANDPA_ATK - GRANDPA_DEF,bulletX,bulletY);
                        HorizontalBullet horizontalBullet = (HorizontalBullet) bullet;
                        horizontalBullet.setToRight(true);
                    }break;
                }
                bullet.setColor(Color.LIGHTGREEN);
                synchronized (bullets){
                    bullets.add(bullet);
                }
            }
        }
//        super.attack();
    }

    @Override
    public void move() {
        //查看方向queue
        synchronized (moveDirections) {
            if (moveDirections.isEmpty() == false) {
                Direction curDirection = moveDirections.pollFirst();//得到第一个
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
    public void cure() {
        synchronized (map) {
            ArrayList<Creature> friends = searchSudokuFriends();
            for (Creature c : friends) {
                c.heal(HEAL_BLOOD);
            }
        }
    }

    public void addMoveDirection(Direction direction) {
        synchronized (moveDirections) {
            moveDirections.add(direction);
        }
    }

    public void clearMoveDirection() {
        synchronized (moveDirections) {
            moveDirections.clear();
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
        this.moveRate = GRANDPA_MOVE_RATE;
    }

    public void addBulletDirection(Direction direction){
        //不能太频繁地发射子弹，要设置时间间隔
        long currentTime = System.currentTimeMillis();
        long timeGap = currentTime - lastTimeSendBullet;
        if(timeGap > BULLET_TIME_GAP)
        {
            synchronized (bulletDirection){
                bulletDirection.add(direction);
            }
            lastTimeSendBullet = currentTime;
        }
    }

    public void clearMoveBulletDirection() {
        synchronized (bulletDirection) {
            bulletDirection.clear();
        }
    }
}
