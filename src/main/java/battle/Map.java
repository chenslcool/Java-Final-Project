package battle;

import bullet.Bullet;
import creature.Creature;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 * @author csl
 * @date 2019/11/24 20:56
 */
public class Map implements Runnable, Config {
    private Creature[][] grounds;
    private int refreshRate;//刷新频率
    private GraphicsContext gc;//通过gc直接绘图
    private BattleState battleState;
    private LinkedList<Bullet> bullets;//display的时候也要显示子弹
    private Image deadImage;
    public Map(BattleState battleState, int refreshRate, GraphicsContext gc,LinkedList<Bullet> bullets) {
        grounds = new Creature[NUM_ROWS][NUM_COLUMNS];//初始化为NULL
        this.battleState = battleState;
        this.refreshRate = refreshRate;
        this.gc = gc;
        this.bullets = bullets;
        URL url = this.getClass().getClassLoader().getResource("pictures/" + "death.jpg");
        deadImage = new javafx.scene.image.Image(url.toString());
    }

    public Creature getCreatureAt(int x, int y) {
        return grounds[x][y];
    }

    public void setCreatureAt(int x, int y, Creature creature) {
        if (grounds[x][y] == null) {//这个位置没有生物
            grounds[x][y] = creature;
        }
    }

    public void removeCreatureAt(int x,int y){
        if(insideMap(x,y))//防止特殊情况
            grounds[x][y] = null;
    }

    public boolean noCreatureAt(int x, int y) {
        return grounds[x][y] == null;
    }

    public void display() {
        //先清空画布
        gc.clearRect(0,0,CANVAS_WIDTH,CANVAS_HEIGHT);
        //先画12*16的网格
        drawBoardLines();
        //绘制所有Creature
        synchronized (this){
            for(int i = 0;i<NUM_ROWS;++i){
                for(int j = 0;j<NUM_COLUMNS;++j){
                    Creature c = this.getCreatureAt(i,j);
                    if(c != null){
                        if(c.isAlive())
                            gc.drawImage(c.getImage(),j*UNIT_SIZE,i*UNIT_SIZE,UNIT_SIZE-1,UNIT_SIZE-1);
                        else{
                            gc.drawImage(deadImage,j*UNIT_SIZE,i*UNIT_SIZE,UNIT_SIZE-1,UNIT_SIZE-1);
                        }
                    }
                }
            }
        }

        //绘制所有的子弹
        synchronized (bullets){//锁住
            for(Bullet bullet:bullets){
                gc.setFill(bullet.getColor());
                gc.fillOval(bullet.getY(),bullet.getX(),BULLTE_RADIUS,BULLTE_RADIUS);
            }
        }
//        System.out.println("bullets.size() = "+bullets.size());
    }

    public void drawBoardLines() {
        //画网格
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(BORDER_LINE_WIDTH);
        for (int i = 0; i <= NUM_ROWS; ++i) {//y 坐标递增
            gc.strokeLine(0,i*UNIT_SIZE,NUM_COLUMNS*UNIT_SIZE,i*UNIT_SIZE);
        }
        for (int i = 0; i <= NUM_COLUMNS; ++i) {// x 坐标递增
            gc.strokeLine(i*UNIT_SIZE,0,i*UNIT_SIZE,NUM_ROWS*UNIT_SIZE);
        }
    }

    public void run() {
        //如果战斗结束或者暂停或结束就退出run(),结束线程
        while (battleState.battleStarted == true&& battleState.battlePaused == false) {
            try {
                TimeUnit.MILLISECONDS.sleep(1000/refreshRate);
                display();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean insideMap(int x, int y) {//坐标是否在地图内
        if (x < 0 || x >= NUM_ROWS || y < 0 || y >= NUM_COLUMNS)
            return false;
        else
            return true;
    }

    public boolean insideMap(double x, double y) {//坐标是否在地图内
        if (x < 0 || x >= NUM_ROWS || y < 0 || y >= NUM_COLUMNS)
            return false;
        else
            return true;
    }
}
