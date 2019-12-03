package bullet;

import battle.Map;
import creature.Creature;

/**
 * @author csl
 * @date 2019/11/27 12:30
 */
//追踪子弹
public class TrackBullet extends Bullet{
    int countDown = TRACK_COUNT_DOWN;
    public TrackBullet(Map map, Creature sender, Creature target, int damage, double x, double y) {
        super(map, sender, target, damage, x, y);
    }

    @Override
    public void move() {
        countDown --;
        //追踪敌人，不管他是不是已经dead了
        //每步的总长度就是STEP_DISTANCE
        //根据敌人的位置设置角度，计算三角函数
        //发射允许有误差，别管上锁
        //向敌人的中心前进
        //敌人死了，就随便走
//        if(target.isAlive() == false){
//            x += STEP_DISTANCE;
//            return;
//        }
        double targetCenterX = target.getPosition().getX()*UNIT_SIZE + UNIT_SIZE/2;
        double targetCenterY = target.getPosition().getY()*UNIT_SIZE + UNIT_SIZE/2;
        double dx = targetCenterX - x;
        double dy = targetCenterY - y;
        double len = Math.sqrt(dx*dx + dy*dy);
        double newX,newY;
        if(STEP_DISTANCE >= len){//这一步超过了敌人
            newX = targetCenterX;
            newY = targetCenterY;
        }
        else{
            double miu = STEP_DISTANCE/len;
            newX = x + dx * miu;
            newY = y + dy * miu;
        }
        x = newX;
        y = newY;
    }

    @Override
    public int getCountDown(){
        return countDown;
    }
}
