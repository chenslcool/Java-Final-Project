package battle;

import bullet.Bullet;
import creature.Evil;
import creature.Huluwa;
import creature.Scorpion;
import creature.Snake;
import formation.Formation;
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
import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author csl
 * @date 2019/11/24 21:47
 */
public class BattleController implements Config {
    ExecutorService pool = Executors.newCachedThreadPool();//线程池

    @FXML
    private BorderPane pane;//主面板
    @FXML
    private Canvas canvas;//在构造函数里面canvas还是null,我也不知道什么时候初始化完成的
    private GraphicsContext gc;//用于在canvas上直接绘图
    Map map;//地图
    BattleState battleState;//战斗状态，由多个线程共享，指示子弹移动、ui刷新线程是否要退出

    private ArrayList<Huluwa> huluwas = new ArrayList<Huluwa>();
    private ArrayList<Evil> evils = new ArrayList<>();
    private Scorpion scorpion;
    private Snake snake;
    private LinkedList<Bullet> bullets = new LinkedList<>();//由于bullets需要经常增删，用链表
    private BulletManager bulletManager;
    public BattleController() {
    }

    @FXML
    private void initialize() {
        pane.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                System.out.println(event.getCode());
                if(event.getCode() == KeyCode.SPACE){
                    startGame();
                }
            }
        });
        Platform.runLater(() -> pane.requestFocus());//否则键盘无效
        //完成画布、地图和葫芦娃等的初始化
        canvas.setWidth(CANVAS_WIDTH);//960
        canvas.setHeight(CANVAS_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        battleState = new BattleState();
        map = new Map(battleState, MAP_REFRESH_RATE, gc,bullets);
        bulletManager = new BulletManager(map,bullets,battleState);
        scorpion = new Scorpion(map,bullets);
        snake = new Snake(map,bullets);
        Formation.transformToHeyi(map,scorpion,snake,evils,bullets);
        initHuluwas();//创建葫芦娃
        //TODO 初始化bulletmanager
        map.display();
    }

    private void initHuluwas() {
        //葫芦娃的初始化，创建了七个葫芦娃，并且排阵
        for (int i = 1; i <= 7; ++i) {
            URL url = this.getClass().getClassLoader().getResource("pictures/" + i + ".jpg");
            Image image = new Image(url.toString());
            huluwas.add(new Huluwa(map, image, "h",bullets));
        }
        transFormChangShe();
    }

    public void arrangeHuluwas(){
        //将葫芦娃排列为长蛇阵型
        //正常情况下，这是在游戏开始之前执行的，此时只有一个main线程，因此不存在对map的竞争访问
        //葫芦娃都先恢复正常状态:aive,Hp...
        for(Huluwa huluwa:huluwas){
            huluwa.resetState();
        }
        //重新排列
        synchronized (map){//其实这个synchronized没必要
            transFormChangShe();
        }
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

    public void startGame(){
        if(battleState.battleStarted == true || battleState.battlePaused == true)//战斗已经开始或者正在暂停
            return;
        //按下空格,开始游戏
        battleState.battleStarted = true;//战斗开始
        arrangeHuluwas();//重新安置葫芦娃
        Formation.transformToHeyi(map,scorpion,snake,evils,bullets);//重新放置所有妖精：恢复状态与阵型
        //葫芦娃线程start
        for(Huluwa huluwa:huluwas){
            pool.execute(huluwa);
        }
        for(Evil evil:evils){
            pool.execute(evil);
        }
        pool.execute(scorpion);
        pool.execute(snake);
        pool.execute(map);//战场刷新线程start
        pool.execute(bulletManager);//子弹移动、出界、伤害

    }

//    public void pauseGame(){
//        //按下
//    }
}
