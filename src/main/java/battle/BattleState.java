package battle;

/**
 * @author csl
 * @date 2019/11/24 21:06
 * 表示战斗的状态:暂停、终止与否,由Map、BulletManager等对象共享，只能由BattleField对象修改
 */

public class BattleState {
    public boolean battlePaused = false;//初始情况：战场没有暂停也没有开始
    public boolean battleStarted = false;
}
