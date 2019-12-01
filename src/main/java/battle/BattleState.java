package battle;

import creature.enumeration.Camp;

/**
 * @author csl
 * @date 2019/11/24 21:06
 * 表示战斗的状态:暂停、终止与否,由Map、BulletManager等对象共享，只能由BattleField对象修改
 */

public class BattleState {
//    private boolean battlePaused = false;//初始情况：战场没有暂停也没有开始
    private boolean battleStarted = false;
    private Camp winner = null;//胜利的一方，在游戏结束时由 map设置
    synchronized public void setStarted(boolean started){
        battleStarted = started;
    }
    synchronized public boolean isBattleStarted(){
        return battleStarted;
    }
    synchronized public void setWinner(Camp winnner){//由map对象调用
        this.winner = winnner;
    }
    synchronized public Camp getWinner(){//侦听线程接受到map线程的notifyAll,侦听到战斗结束，侦听线程调用gameOver,调用此方法
        //获得战斗结果信息
        return winner;
    }

}
