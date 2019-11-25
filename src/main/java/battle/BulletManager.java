package battle;

import bullet.Bullet;
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
