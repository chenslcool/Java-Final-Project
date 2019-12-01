package record;

import creature.Creature;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author csl
 * @date 2019/12/1 16:36
 * 一盘对战的完整记录，对于每一帧需要记录：子弹位置、状态（发射者）、所有现存生物的类型（确定图像）、位置、状态、血量
 */
public class Record implements Serializable {
    public ArrayList<CreatureRecord> creatureRecords;
    public ArrayList<BulletRecord> bulletRecords;
    public Record(){
        creatureRecords = new ArrayList<>();
        bulletRecords = new ArrayList<>();
    }
}
