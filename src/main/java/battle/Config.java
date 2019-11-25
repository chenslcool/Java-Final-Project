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


}
