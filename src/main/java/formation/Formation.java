package formation;

import annotations.Info;
import battle.Config;
import battle.Map;
import bullet.Bullet;
import creature.Creature;
import creature.Evil;
import creature.Scorpion;
import creature.Snake;

import java.util.ArrayList;
import java.util.LinkedList;

import static formation.FormationKind.*;

/**
 * @author csl
 * @date 2019/11/25 12:48
 */

@Info(description = "all formations of Evil Camp")
public class Formation implements Config {
    //将所有变换函数设置为static函数:接受一个Creature(蝎子精)作为leader，剩余的妖怪(包括蛇精)是在Arraylist中
    //TODO 添加所有static阵法方法
    //阵型变换只有在战斗还未开始的时候才能进行，因此理论在main线程上对map的访问是不会和其他线程竞争的
    //attention 默认是在葫芦娃恢复原始阵型后再进行妖怪的变换的，因此理论上不会位置冲突，单为了保险还是同步

    private static FormationKind nextFormation = HEYI;//初始化为鹤翼

    public static void transFormToNextFormation(Map map, Scorpion scorpion, Snake snake, ArrayList<Evil> evils, LinkedList<Bullet> bullets) {
        //变换成下一个阵型
        switch (nextFormation) {
            case HEYI: {
                transformToHeyi(map, scorpion, snake, evils, bullets);
                nextFormation = YANXING;
            }
            break;
            case YANXING: {
                transFormToYanxing(map, scorpion, snake, evils, bullets);
                nextFormation = CHANGSHE;
            }
            break;
            case CHANGSHE: {
                transFormToChangShe(map, scorpion, snake, evils, bullets);
                nextFormation = ChongE;
            }
            break;
            case ChongE:{
                transFormToChongE(map, scorpion, snake, evils, bullets);
                nextFormation = YuLin;
            }break;
            case YuLin:{
                transFormToYuLin(map, scorpion, snake, evils, bullets);
                nextFormation = Fan;
            }break;
            case Fan:{
                transFormToFan(map, scorpion, snake, evils, bullets);
                nextFormation = Moon;
            }break;
            case Moon:{
                transFormToMoon(map, scorpion, snake, evils, bullets);
                nextFormation = FenShi;
            }break;
            case FenShi:{
                transFormToFenshi(map, scorpion, snake, evils, bullets);
                nextFormation = HEYI;
            }break;
        }
        ;
    }

    public static void clearPreFormation(Map map, Scorpion scorpion, Snake snake, ArrayList<Evil> evils, LinkedList<Bullet> bullets) {
        synchronized (map) {
            //清除位置部分可以单独拿出来作为clear方法
            int rmX, rmY;//删除的位置
            //把蝎子精和蛇精从map移除
            //如果是游戏刚开始的状态，蝎子精和蛇精的位置是无效的
            rmX = scorpion.getPosition().getX();
            rmY = scorpion.getPosition().getY();
            map.removeCreatureAt(rmX, rmY);
            rmX = snake.getPosition().getX();
            rmY = snake.getPosition().getY();
            map.removeCreatureAt(rmX, rmY);
            for (Evil evil : evils) {
                //先把这些妖精从map上移除
                rmX = evil.getPosition().getX();
                rmY = evil.getPosition().getY();
                map.removeCreatureAt(rmX, rmY);
            }
            evils.clear();//删除所有妖怪
        }
    }

    public static void transformToHeyi(Map map, Scorpion scorpion, Snake snake, ArrayList<Evil> evils, LinkedList<Bullet> bullets) {

        synchronized (map) {
            clearPreFormation(map, scorpion, snake, evils, bullets);
            //重置阵型,占据右半边
            int leaderX = 5;
            int leaderY = 10;
            scorpion.moveTo(leaderX, leaderY);
            snake.moveTo(5, 14);
            for (int i = 1; i <= 2; ++i) {
                Evil evil = new Evil(map, bullets);
                evil.moveTo(leaderX - i, leaderY + i);//这里面也会对map上锁，不过都是在一个线程，问题不大
                evils.add(evil);
                evil = new Evil(map, bullets);
                evil.moveTo(leaderX + i, leaderY + i);
                evils.add(evil);
            }
        }
    }

    public static void transFormToYanxing(Map map, Scorpion scorpion, Snake snake, ArrayList<Evil> evils, LinkedList<Bullet> bullets) {
        synchronized (map) {
            clearPreFormation(map, scorpion, snake, evils, bullets);
            //重置阵型,占据右半边
            int leaderX = 5;
            int leaderY = 12;
            scorpion.moveTo(leaderX, leaderY);
            snake.moveTo(5, 14);
            for (int i = 1; i <= 3; ++i) {
                Evil evil = new Evil(map, bullets);
                evil.moveTo(leaderX - i, leaderY + i);//这里面也会对map上锁，不过都是在一个线程，问题不大
                evils.add(evil);
                evil = new Evil(map, bullets);
                evil.moveTo(leaderX + i, leaderY - i);
                evils.add(evil);
            }
        }
    }

    public static void transFormToChangShe(Map map, Scorpion scorpion, Snake snake, ArrayList<Evil> evils, LinkedList<Bullet> bullets) {
        synchronized (map) {
            clearPreFormation(map, scorpion, snake, evils, bullets);
            //重置阵型,占据右半边
            int leaderX = 5;
            int leaderY = 10;
            scorpion.moveTo(leaderX, leaderY);
            snake.moveTo(5, 14);
            for (int i = 1; i <= 7; ++i) {
                Evil evil = new Evil(map, bullets);
                evil.moveTo(1 + i, 12);//这里面也会对map上锁，不过都是在一个线程，问题不大
                evils.add(evil);
            }
        }
    }

    public static void transFormToChongE(Map map, Scorpion scorpion, Snake snake, ArrayList<Evil> evils, LinkedList<Bullet> bullets){
        synchronized (map) {
            clearPreFormation(map, scorpion, snake, evils, bullets);
            //重置阵型,占据右半边
            int leaderX = 9;
            int leaderY = 11;
            scorpion.moveTo(leaderX, leaderY);
            snake.moveTo(6,12);
            for (int i = 0; i <= 2; ++i) {
                Evil evil = new Evil(map, bullets);
                evil.moveTo(leaderX -1 -2*i, leaderY);//这里面也会对map上锁，不过都是在一个线程，问题不大
                evils.add(evil);
                evil = new Evil(map, bullets);
                evil.moveTo(leaderX -2 -2*i, leaderY + 1);//这里面也会对map上锁，不过都是在一个线程，问题不大
                evils.add(evil);
            }
        }
    }

    public static void transFormToYuLin(Map map, Scorpion scorpion, Snake snake, ArrayList<Evil> evils, LinkedList<Bullet> bullets){
        synchronized (map) {
            clearPreFormation(map, scorpion, snake, evils, bullets);
            //重置阵型,占据右半边
            int leaderX = 7;
            int leaderY = 12;
            scorpion.moveTo(leaderX, leaderY);
            snake.moveTo(4,12);
            for(int i = 0;i<5;++i){
                Evil evil = new Evil(map, bullets);
                evil.moveTo(leaderX-1,leaderY-2+i);
                evils.add(evil);
            }
            for(int i = 0;i<3;++i){
                Evil evil = new Evil(map, bullets);
                evil.moveTo(leaderX-2,leaderY-1+i);
                evils.add(evil);
            }
        }
    }

    public static void transFormToFan(Map map, Scorpion scorpion, Snake snake, ArrayList<Evil> evils, LinkedList<Bullet> bullets){
        synchronized (map) {
            clearPreFormation(map, scorpion, snake, evils, bullets);
            //重置阵型,占据右半边
            int leaderX = 3;
            int leaderY = 11;
            scorpion.moveTo(leaderX, leaderY);
            snake.moveTo(6,11);
            for(int i = 0;i<2;++i){
                Evil evil = new Evil(map, bullets);
                evil.moveTo(leaderX + i + 2,leaderY  - 1 - i);
                evils.add(evil);
                evil = new Evil(map, bullets);
                evil.moveTo(leaderX + i + 2,leaderY  + 1 + i);
                evils.add(evil);
            }
            Evil evil = new Evil(map, bullets);
            evil.moveTo(leaderX + 4,leaderY  + 1);
            evils.add(evil);
            evil = new Evil(map, bullets);
            evil.moveTo(leaderX + 4,leaderY  - 1);
            evils.add(evil);
            evil = new Evil(map, bullets);
            evil.moveTo(leaderX + 5,leaderY );
            evils.add(evil);
            evil = new Evil(map, bullets);
            evil.moveTo(leaderX + 1,leaderY );
            evils.add(evil);

        }
    }

    public static void transFormToMoon(Map map, Scorpion scorpion, Snake snake, ArrayList<Evil> evils, LinkedList<Bullet> bullets){
        synchronized (map) {
            clearPreFormation(map, scorpion, snake, evils, bullets);
            //重置阵型,占据右半边
            int leaderX = 6;
            int leaderY = 9;
            scorpion.moveTo(leaderX, leaderY);
            snake.moveTo(6,13);
            for(int i = 0;i < 3;++i)
            {
                for(int j =0;j<3;++j){
                    Evil evil = new Evil(map,bullets);
                    evil.moveTo(leaderX-1+i,leaderY+1+j);
                    evils.add(evil);
                }
            }
            for(int i =0;i<2;++i){
                Evil evil = new Evil(map,bullets);
                evil.moveTo(leaderX-2-i,leaderY+2+i);
                evils.add(evil);
                evil = new Evil(map,bullets);
                evil.moveTo(leaderX+2+i,leaderY+2+i);
                evils.add(evil);
            }
            for(int i =0;i<3;++i){
                Evil evil = new Evil(map,bullets);
                evil.moveTo(leaderX-2-i,leaderY+3+i);
                evils.add(evil);
                evil = new Evil(map,bullets);
                evil.moveTo(leaderX+2+i,leaderY+3+i);
                evils.add(evil);
            }
        }
    }

    public static void transFormToFenshi(Map map, Scorpion scorpion, Snake snake, ArrayList<Evil> evils, LinkedList<Bullet> bullets){
        synchronized (map) {
            clearPreFormation(map, scorpion, snake, evils, bullets);
            //重置阵型,占据右半边
            int leaderX = 4;
            int leaderY = 11;
            scorpion.moveTo(leaderX, leaderY);
            snake.moveTo(leaderX+1,leaderY);
            for(int i = 0;i < 3;++i)
            {
                Evil evil = new Evil(map,bullets);
                evil.moveTo(leaderX+1+i,leaderY+1+i);
                evils.add(evil);
                evil = new Evil(map,bullets);
                evil.moveTo(leaderX+1+i,leaderY-1-i);
                evils.add(evil);
            }
            for(int i =0;i<3;++i){
                Evil evil = new Evil(map,bullets);
                evil.moveTo(leaderX+2+i,leaderY);
                evils.add(evil);
            }
        }
    }


}
