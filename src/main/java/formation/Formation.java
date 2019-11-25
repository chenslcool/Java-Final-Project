package formation;

import battle.Config;
import battle.Map;
import creature.Creature;
import creature.Evial;
import creature.Scorpion;
import creature.Snake;

import java.util.ArrayList;

/**
 * @author csl
 * @date 2019/11/25 12:48
 */

//只有在游戏还未开始的时候才能调用formation的变换函数修改妖精的排列
public class Formation implements Config {
    //将所有变换函数设置为static函数:接受一个Creature(蝎子精)作为leader，剩余的妖怪(包括蛇精)是在Arraylist中
    //TODO 添加所有static阵法方法
    //阵型变换只有在战斗还未开始的时候才能进行，因此理论在main线程上对map的访问是不会和其他线程竞争的
    //attention 默认是在葫芦娃恢复原始阵型后再进行妖怪的变换的，因此理论上不会位置冲突，单为了保险还是同步
    public static void transformToHeyi(Map map, Scorpion scorpion, Snake snake, ArrayList<Evial> evials){

        synchronized (map){
            //清除位置部分可以单独拿出来作为clear方法
            int rmX,rmY;//删除的位置
            //把蝎子精和蛇精从map移除
            rmX = scorpion.getPosition().getX();
            rmY = scorpion.getPosition().getY();
            map.removeCreatureAt(rmX,rmY);
            rmX = snake.getPosition().getX();
            rmY = snake.getPosition().getY();
            map.removeCreatureAt(rmX,rmY);
            for(Evial evial:evials){
                //先把这些妖精从map上移除
                rmX = evial.getPosition().getX();
                rmY = evial.getPosition().getY();
                map.removeCreatureAt(rmX,rmY);
            }
            evials.clear();//删除所有妖怪
            //重置阵型,占据右半边
            int leaderX = 5;
            int leaderY = 10;
            for(int i = 1;i <= 2;++i){
                Evial evial = new Evial(map);
                evial.moveTo(leaderX-i,leaderY+i);//这里面也会对map上锁，不过都是在一个线程，问题不大
                evials.add(evial);
                evial = new Evial(map);
                evial.moveTo(leaderX+i,leaderY+i);
                evials.add(evial);
            }
        }
    }
}
