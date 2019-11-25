package battle;

import creature.Huluwa;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author csl
 * @date 2019/11/24 21:47
 */
public class BattleController implements Config {
    ExecutorService pool = Executors.newCachedThreadPool();//线程池

    private ArrayList<Huluwa> huluwas = new ArrayList<Huluwa>();
    @FXML
    private BorderPane pane;//主面板
    @FXML
    private Canvas canvas;//在构造函数里面canvas还是null,我也不知道什么时候初始化完成的
    private GraphicsContext gc;//用于在canvas上直接绘图
    Map map;//地图
    BattleState battleState;//战斗状态，由多个线程共享，指示子弹移动、ui刷新线程是否要退出

    public BattleController() {
    }

    @FXML
    private void initialize() {
        pane.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                System.out.println(event.getCode());
                if(event.getCode() == KeyCode.C){
                    gc.clearRect(0,0,CANVAS_WIDTH,CANVAS_HEIGHT);
                }
            }
        });
        Platform.runLater(() -> pane.requestFocus());//否则键盘无效
        //完成画布、地图和葫芦娃等的初始化
        canvas.setWidth(CANVAS_WIDTH);//960
        canvas.setHeight(CANVAS_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        battleState = new BattleState();
        map = new Map(battleState, MAP_REFRESH_RATE, gc);
        initHuluwas();

        //试一下创建生物线程、map线程
        for(Huluwa huluwa:huluwas){
            pool.execute(huluwa);
        }
        pool.execute(map);
//        gc.setFill(Color.GREEN);
//        gc.setStroke(Color.BLUE);
//        gc.setLineWidth(5);
//        gc.strokeLine(40, 10, 10, 40);


    }

    private void initHuluwas() {
        //初始化七个葫芦娃并且设置初始常设阵型
        for (int i = 1; i <= 7; ++i) {
            URL url = this.getClass().getClassLoader().getResource("pictures/" + i + ".jpg");
            Image image = new Image(url.toString());
            huluwas.add(new Huluwa(map, image, "h"));
        }
        transFormChangShe();
    }

    private void transFormChangShe() {
        //将葫芦娃重置为长蛇阵型，需要修改map和huluwa数组,调用Creature的moveTo方法
        int y = NUM_COLUMNS/4;
        int x = NUM_ROWS/2 - 3;
        for (Huluwa huluwa : huluwas) {
            huluwa.moveTo(x, y);
            ++x;
        }
    }
}
