package creature;

import battle.Map;
import bullet.Bullet;
import bullet.StraightBullet;
import creature.Controllable;
import creature.Creature;
import creature.enumeration.Direction;
import javafx.scene.paint.Color;

import java.util.LinkedList;

/**
 * @author csl
 * @date 2019/12/19 23:48
 */
public abstract class JusticeCreature extends Creature implements Controllable {
    protected boolean isControlled;
    protected LinkedList<Direction> moveDirections = new LinkedList<Direction>();
    protected LinkedList<Direction> bulletDirection = new LinkedList<>();

    public JusticeCreature(Map map, LinkedList<Bullet> bullets){
        super(map,bullets);
        this.isControlled = false;
    }

    @Override
    public void setControlled(boolean controlled) {
        this.isControlled = controlled;
        //改变速度
        if(controlled){
            this.moveRate *= CONTROL_TIMES;
            System.out.println("move rate double,current = "+moveRate);
        }
        else{
            this.moveRate /= CONTROL_TIMES;
            System.out.println("move rate half,current = "+moveRate);
        }
    }

    @Override
    public boolean underControlled() {
        return isControlled;
    }

    @Override
    public void controlMove() {
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
    }

    @Override
    public void controlAttack() {
        synchronized (bulletDirection){
            if(bulletDirection.isEmpty() == false){
                Direction direction = bulletDirection.pollLast();
                int x = position.getX();
                int y = position.getY();
                double bulletX = x * UNIT_SIZE + (UNIT_SIZE - BULLTE_RADIUS) / 2;
                double bulletY = y * UNIT_SIZE + (UNIT_SIZE - BULLTE_RADIUS) / 2;
                Bullet bullet = null;
                try {
                    bullet = bulletGenerator.getBullet(this,null,GRANDPA_ATK - GRANDPA_DEF,bulletX,bulletY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                StraightBullet straightBullet = (StraightBullet) bullet;
                straightBullet.setDirection(direction);
                synchronized (bullets){
                    bullets.add(bullet);
                }
            }
        }
    }

    @Override
    public void addMoveDirection(Direction direction) {
        synchronized (moveDirections) {
            moveDirections.add(direction);
        }
    }

    @Override
    public void addAttackDirection(Direction direction) {
        //暂时未考虑过于频繁的发射
        synchronized (bulletDirection){
            bulletDirection.add(direction);
        }
    }

    @Override
    public void clearMoveDirection() {
        synchronized (moveDirections) {
            moveDirections.clear();
        }
    }

    @Override
    public void clearAttackDirection() {
        synchronized (bulletDirection) {
            bulletDirection.clear();
        }
    }

    @Override
    public void move(){
        if(isControlled){//如果是控制状态，就按照指令move
            controlMove();
        }
        else{
            super.move();//随机移动
        }
    }

    @Override
    public void attack(){
        if(isControlled){
            controlAttack();
        }
        else{
            super.attack();
        }
    }

    @Override
    public boolean canBeControlled(){
        return this.alive;
    }
}
