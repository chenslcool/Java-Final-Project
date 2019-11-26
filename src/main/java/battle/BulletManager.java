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
                    if(entireBulletInside){//完整在放个内
                        synchronized (map){
                            Creature c = map.getCreatureAt(squareX,squareY);
                            //如果这个地方有生物并且是敌人
////                            synchronized (c){//锁住生物，这时候只有这个自当能攻击它
////
//                            }
                            //仅有本线程(Manager)掌控子弹的移动和攻击，所以不会出现一个生物被多个同时击中、杀死
                            if(c != null && c.getCamp() != bullet.getSender().getCamp() && c.isAlive()){
                                c.getHurt(bullet.getDamage());//受到伤害
                                it.remove();//删除子弹
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
