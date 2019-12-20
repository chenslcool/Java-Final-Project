package record;

import annotations.Info;
import creature.enumeration.Camp;

import java.io.Serializable;

/**
 * @author csl
 * @date 2019/12/1 20:16
 * 记录战场上的一帧的一个生物信息(不论存活状态)，一帧需要用arraylist<CreatureRecord>表示
 */
@Info(description = "record one creature on court")
public class CreatureRecord implements Serializable {
    public int x;
    public int y;
    public Camp camp;
    public int currentHP;
    public boolean alive;
    public String type;//生物名称,用于确定用什么表示
    public boolean isCurable;
    public boolean isControlled;
    public CreatureRecord(int x, int y, Camp camp, int currentHP, boolean alive, String type,boolean isControlled,boolean isCurable) {
        this.x = x;
        this.y = y;
        this.camp = camp;
        this.currentHP = currentHP;
        this.alive = alive;
        this.type = type;
        this.isControlled = isControlled;
        this.isCurable = isCurable;
    }
    @Override
    public  String toString(){
        return type+"("+x+","+y+")";
    }
}
