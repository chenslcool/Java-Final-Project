package battle;

import annotations.Info;

/**
 * @author csl
 * @date 2019/11/24 20:57
 * 战场设置
 */
@Info(description = "the configuration of battle:size of canvas,move rate,attack and defense value...")
public interface Config {
    //画布信息:12*16,每个单元格: 45*45，注意canvas是以左上角为原点的
    int CANVAS_WIDTH = 960;//Width和Height要成比例4:3
    int CANVAS_HEIGHT = 720;

    int NUM_ROWS = 12;//对应x
    int NUM_COLUMNS = 16;//对应y
    int MAP_REFRESH_RATE = 240;

    int UNIT_SIZE = CANVAS_HEIGHT / NUM_ROWS;
    int BORDER_LINE_WIDTH = 1;

    //生物的一些默认属性
    int DEFAULT_MAX_HP = 100;
    int DEFAULT_ATTACK_VALUE = 60;
    int DEFAULT_DEFENSE_VALUE = 40;
    int DEFAULT_MOVE_RATE = 2;//生物涉及刷新频率
//    int GRANDPA_MOVE_RATE = DEFAULT_MOVE_RATE * 2;//为了更快相应玩家方向控制
    //不同生物的定制属性
    int GRANDPA_ATK = 50;//老爷爷的攻击力比较弱，主要任务是回血
    int GRANDPA_DEF = 40;//防御力也很弱
    int HULUWA_ATK = 55;
    int HULUWA_DEF = 35;
    int EVIL_ATK = 55;
    int EVIL_DEF = 40;
    int EVIL_LEADER_ATK = 65;
    int EVIL_LEADER_DEF = 40;
    //子弹
    double STEP_DISTANCE = 1;//子弹每次移动的距离：像素
    int BULLTE_RADIUS = 20;//子弹半径
    //    int DEFAULT_BULLET_DAMAGE = 30;
    int BULLET_REFRESH_RATE = 160;
    int TRACK_COUNT_DOWN = 1500;//子弹最多追踪的步数
    //玩家控制发射子弹最小时间间隔，防止太频繁地发射子弹
    long BULLET_TIME_GAP = 200;//200 millisecond = 0.2s
    //血条
    int BLOOD_LINE_WIDTH = 4;

    //回血
    int HEAL_BLOOD = 20;
}
