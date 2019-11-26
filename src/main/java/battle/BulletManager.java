package battle;

import bullet.Bullet;
import creature.Creature;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

/**
 * @author csl
 * @date 2019/11/25 19:17
 */
//管理所有子弹的移动、出界、爆炸与生物的伤害
public class BulletManager implements Config,Runnable{
    private LinkedList<Bullet> bullets;
    private Map map;
    private BattleState battleState;
    public BulletManager(Map map,LinkedList<Bullet> bullets,BattleState battleState){
        this.map = map;
        this.bullets = bullets;
        this.battleState = battleState;
    }
    public void moveAll(){
        //移动每一颗子弹，检测是否碰到生物
        synchronized (bullets){
            ListIterator<Bullet> it = bullets.listIterator();
            while(it.hasNext()){
                Bullet bullet = it.next();
                bullet.move();
                if(bullet.outOfMap()){
                    it.remove();//删除
                }
                else{
                    //判断有没有碰到敌人
                    //TODO 需要更加精细地判断有没有敌人
                    //计算子弹圆心的坐标，判断是否完整地在整个生物体之内
                    double centerX =  bullet.getX() + BULLTE_RADIUS/2;
                    double centerY =  bullet.getY() + BULLTE_RADIUS/2;
                    int squareX = (int)(centerX/UNIT_SIZE);
                    int squareY = (int)(centerY/UNIT_SIZE);
                    boolean entireBulletInside = (centerX - BULLTE_RADIUS/2 > squareX*UNIT_SIZE)
                            &&(centerX + BULLTE_RADIUS/2 < (squareX+1)*UNIT_SIZE)
                            &&(centerY - BULLTE_RADIUS/2 > squareY*UNIT_SIZE)
                            &&(centerY + BULLTE_RADIUS/2 < (squareY+1)*UNIT_SIZE);
                    if(entireBulletInside){//子弹完整地在一个方格内
                        synchronized (map){
                            Creature c = map.getCreatureAt(squareX,squareY);
                            //仅有本线程(Manager)掌控子弹的移动和攻击，所以不会出现一个生物被多个同时击中、杀死
                            //但是由于生物线程也在进行，可能本线程认为打中了，而在极短时间内目标移动了，却还是受伤 or 死亡了，
                            //视觉效果可能是：墓碑移动(打死 -> 生物线程已经进入 while(alive)循环,move() -> ui刷新显示墓碑 -> 移动后再次刷新)
                            //解决方式: 给生物加锁？move和gethurt不能同时进行? 会不会死锁
                            //如果申请锁的顺序一致 map -> creature应该不会死锁
                            if(c!=null)
                            {
                                synchronized (c){//不能移动
                                    if(c.getCamp() != bullet.getSender().getCamp() && c.isAlive()){
                                        c.getHurt(bullet.getDamage());//受到伤害
                                        it.remove();//删除子弹
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    @Override
    public void run() {
        while(battleState.battleStarted == true || battleState.battlePaused == false){
            try {
                TimeUnit.MILLISECONDS.sleep(1000/BULLET_REFRESH_RATE);
                moveAll();//移动所有子弹
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
