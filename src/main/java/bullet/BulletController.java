package bullet;

import battle.BattleState;
import battle.Config;
import battle.Map;
import bullet.Bullet;
import bullet.TrackBullet;
import creature.Creature;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

/**
 * @author csl
 * @date 2019/11/25 19:17
 * manage all bullets on court
 */
public class BulletController implements Config, Runnable {
    private LinkedList<Bullet> bullets;
    private Map map;
    private BattleState battleState;

    public BulletController(Map map, LinkedList<Bullet> bullets, BattleState battleState) {
        this.map = map;
        this.bullets = bullets;
        this.battleState = battleState;
    }

    public void moveAll() {//是在Manager线程中单独执行的
        //移动每一颗子弹，检测是否碰到生物
        synchronized (bullets) {//移动所有子弹过程中不能增加子弹
            ListIterator<Bullet> it = bullets.listIterator();
            while (it.hasNext()) {
                //应该先判断是否伤害敌人 -> 出界 -> 移动
                //这是都是未出界状态
                Bullet bullet = it.next();
                //判断有没有碰到敌人
                //TODO 需要更加精细地判断有没有敌人
                //计算子弹圆心的坐标，判断是否完整地在整个生物体之内
                double centerX = bullet.getX() + BULLTE_RADIUS / 2;
                double centerY = bullet.getY() + BULLTE_RADIUS / 2;
                int squareX = (int) (centerX / UNIT_SIZE);
                int squareY = (int) (centerY / UNIT_SIZE);
                //下面这个完全在方格内的条件太强了，导致视觉上击中了，实际上没击中
//                boolean entireBulletInside = (centerX - BULLTE_RADIUS/2 > squareX*UNIT_SIZE)
//                        &&(centerX + BULLTE_RADIUS/2 < (squareX+1)*UNIT_SIZE)
//                        &&(centerY - BULLTE_RADIUS/2 > squareY*UNIT_SIZE)
//                        &&(centerY + BULLTE_RADIUS/2 < (squareY+1)*UNIT_SIZE);

                if (true) {//子弹完整地在一个方格内
                    Creature c = map.getCreatureAt(squareX, squareY);
                    //仅有本线程(Manager)掌控子弹的移动和攻击，所以不会出现一个生物被多个同时击中、杀死
                    //但是由于生物线程也在进行，可能本线程认为打中了，而在极短时间内目标移动了，却还是受伤 or 死亡了，
                    //视觉效果可能是：墓碑移动(打死 -> 生物线程已经进入 while(alive)循环,move() -> ui刷新显示墓碑 -> 移动后再次刷新)
                    //解决方式: 给生物加锁？move和gethurt不能同时进行? 会不会死锁
                    //如果申请锁的顺序一致 map -> creature应该不会死锁
                    if (c != null) {
                        synchronized (c) {//不能移动,和生物run获得锁的顺序一样 map -> creature
                            if ((c.getCamp() != bullet.getSender().getCamp()) && c.isAlive()) {//是敌方生物并且活着
                                c.getHurt(bullet.getDamage());//受到伤害
                                it.remove();//删除子弹
                                continue;
                            }
                        }
                    }
                }
                //如果子弹没有爆炸
                bullet.move();//子弹移动,为什么trackBullet敌人死后会出界而不被回收呢?导致rte:越界
                if (bullet.outOfMap() ||
                        (bullet instanceof TrackBullet && ((bullet.getTarget().isAlive() == false) || bullet.getCountDown() == 0))) {//如果这次移动导致出界
                    it.remove();//删除
                }
            }
        }
    }

    /**
     * keep moving all bullets,checking each one boom,outside or not,manage the bullet list
     * until battle end or interrupted
     */
    @Override
    public void run() {
        while (battleState.isBattleStarted() && Thread.interrupted() == false) {
            try {
                synchronized (battleState){
                    while (battleState.gamePaused()){
                        System.out.println("game pause,bulletController waiting for continue!");
                        battleState.wait();//如果战斗暂停，就等待战斗继续,notifyAll()在gameContinue()中调用
                    }
                }
                TimeUnit.MILLISECONDS.sleep(1000 / BULLET_REFRESH_RATE);
                synchronized (map) {//上锁顺序 map -> creature
                    moveAll();//移动所有子弹
                }
            } catch (InterruptedException e) {
                break;//sleep的时候被shutdown
            }
        }
        System.out.println("game over,bulletController.run() exit");
    }

    /**
     * remove all bullets,call when game over
     */
    public void clearBullets() {
        bullets.clear();
    }
}
