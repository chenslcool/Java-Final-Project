package record;

import creature.enumeration.Camp;

import java.io.Serializable;

/**
 * @author csl
 * @date 2019/12/1 20:16
 * 记录战场上的一帧的一个生物信息(不论存活状态)，一帧需要用arraylist<CreatureRecord>表示
 */
public class CreatureRecord implements Serializable {
    public Camp camp;
    public int currentHP;
    public boolean alive;
    public String type;//生物名称,用于确定用什么表示
    public CreatureRecord(Camp camp,int currentHP,boolean alive,String type){
        this.camp = camp;
        this.currentHP = currentHP;
        this.alive = alive;
        this.type = type;
    }

}
