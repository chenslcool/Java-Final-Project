package battle;

/**
 * @author csl
 * @date 2019/11/24 20:57
 */
public interface Config {
    //画布信息:12*16,每个单元格: 45*45，注意canvas是以左上角为原点的
    int CANVAS_WIDTH = 960;//Width和Height要成比例4:3
    int CANVAS_HEIGHT = 720;

    int NUM_ROWS = 12;//对应x
    int NUM_COLUMNS = 16;//对应y
    int MAP_REFRESH_RATE = 10;

    int UNIT_SIZE = CANVAS_HEIGHT/NUM_ROWS;
    int BORDER_LINE_WIDTH = 1;

    //生物的一些默认属性
    int DEFAULT_MAX_HP = 100;
    int DEFAULT_ATTACK_VALUE = 60;
    int DEFAULT_DEFENSE_VALUE = 40;
    int DEFAULT_MOVE_RATE = 2;//涉及刷新频率

    //不同生物的定制属性
    int GRANDPA_ATK = 40;//老爷爷的攻击力比较弱，主要任务是回血
    int GRANDPA_DEF = 30;//防御力也很弱
    int HULUWA_ATK = 50;
    int HULUWA_DEF = 35;
    int EVIL_ATK = 55;
    int EVIL_DEF = 40  ;
    int EVIL_LEADER_ATK = 60;
    int EVIL_LEADER_DEF = 40;
    //子弹
    double STEP_DISTANCE = 40;//子弹每次移动的距离：像素
    int BULLTE_RADIUS = 20;//子弹半径
//    int DEFAULT_BULLET_DAMAGE = 30;
    int BULLET_REFRESH_RATE = 5;

    //血条
    int BLOOD_LINE_WIDTH = 4;

    //回血
    int HEAL_BLOOD = 20;
}
