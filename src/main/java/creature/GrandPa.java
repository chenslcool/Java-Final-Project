package creature;

import battle.Map;
import bullet.Bullet;
import bullet.HorizontalBulletFactory;
import creature.enumeration.Camp;
import creature.enumeration.Direction;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author csl
 * @date 2019/11/27 19:16
 */
public class GrandPa extends Creature implements Curable {
    private LinkedList<Direction> directions = new LinkedList<Direction>();
    public GrandPa(Map map, Image image, String name, LinkedList<Bullet> bullets){
        super(map,bullets);
        this.image = image;
        this.name = name;
        this.camp = Camp.JUSTICE;
        bulletBulletFactory = new HorizontalBulletFactory();
    }
    @Override
    public void attack(){
        //老爷爷的attack(这个方法名不好，应该改成act)就是移动，治愈队友
        //治愈九宫格之内的
        synchronized (map){
            cure();
        }
        super.attack();
    }

    @Override
    public void move(){
        //查看方向queue
        synchronized (directions){
            if(directions.isEmpty() == false){
                Direction curDirection = directions.pollFirst();//得到第一个
                int newX = position.getX();
                int newY = position.getY();
                switch (curDirection){
                    case UP:{
                        newX -= 1;
                    }break;
                    case DOWN:{
                        newX += 1;
                    }break;
                    case RIGHT:{
                        newY += 1;
                    }break;
                    default:{
                        newY -= 1;
                    }
                }
                if(map.insideMap(newX,newY) && map.noCreatureAt(newX,newY)){
                    map.removeCreatureAt(position.getX(),position.getY());
                    map.setCreatureAt(newX,newY,this);//放置自己
                    synchronized (this){//锁住自己
                        setPosition(newX,newY);
                    }
                    System.out.println("move");
//                    return;//按键方向成功
                }
            }
        }
//        super.move();//不成功就用之前的move
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

    public void addDirection(Direction direction){
        synchronized (directions){
            directions.add(direction);
        }
    }
    public void clearDirection(){
        synchronized (directions){
            directions.clear();
        }
    }
}
