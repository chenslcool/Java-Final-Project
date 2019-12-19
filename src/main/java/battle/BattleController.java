package battle;

import annotations.Info;
import bullet.Bullet;
import bullet.BulletController;
import creature.*;
import creature.enumeration.Direction;
import formation.Formation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.*;

/**
 * @author csl
 * @date 2019/11/24 21:47
 */

@Info(description = "handle keyboard event,initialize battle components.Generally,control the whole battle")
public class BattleController implements Config {
    public static Stage stage = null;
    ExecutorService pool;//线程池
    int n = 0;
    @FXML
    private BorderPane pane;//主面板
    @FXML
    private Canvas canvas;//在构造函数里面canvas还是null,我也不知道什么时候初始化完成的
    private GraphicsContext gc;//用于在canvas上直接绘图
    Map map;//地图
    BattleState battleState;//战斗状态，由多个线程共享，指示子弹移动、ui刷新线程是否要退出
    private ArrayList<Huluwa> huluwas = new ArrayList<Huluwa>();
    private GrandPa grandPa;
    private ArrayList<Evil> evils = new ArrayList<>();
    private Scorpion scorpion;
    private Snake snake;
    private LinkedList<Bullet> bullets = new LinkedList<>();//由于bullets需要经常增删，用链表
    private BulletController bulletController;
    private ObjectOutputStream writer;//每次刷新就向文件写
    private ObjectInputStream reader;
    Timeline timeline;

    public BattleController() {
    }

    @FXML
    private void initialize() {
        pane.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.SPACE) {
                    if(battleState.isInFreeState())
                        startGame();//里面已经对战斗状态进行了判断
                    else if(battleState.isBattleStarted()){
                        if(battleState.gamePaused() == false){
                            //如果战斗正在进行，按下空格就是暂停
                            System.out.println("pause game!");
                            pauseGame();
                        }
                        else{
                            //正在暂停模式。按下空格继续
                            System.out.println("continue game!");
                            continueGame();
                        }
                    }
                } else if (event.getCode() == KeyCode.L && battleState.isInFreeState()) {
                    review();//里面已经对战斗状态进行了判断
                } else if (event.getCode() == KeyCode.F && battleState.isInFreeState()) {
                    //在战斗还没开始可以变换阵型
                    Formation.transFormToNextFormation(map, scorpion, snake, evils, bullets);
                    map.display(false);//writer已经关闭
                } else if (event.getCode() == KeyCode.UP && battleState.isBattleStarted()) {
                    grandPa.addMoveDirection(Direction.UP);
                } else if (event.getCode() == KeyCode.DOWN && battleState.isBattleStarted()) {
                    grandPa.addMoveDirection(Direction.DOWN);
                } else if (event.getCode() == KeyCode.RIGHT && battleState.isBattleStarted()) {
                    grandPa.addMoveDirection(Direction.RIGHT);
                } else if (event.getCode() == KeyCode.LEFT && battleState.isBattleStarted()) {
                    grandPa.addMoveDirection(Direction.LEFT);
                } else if (event.getCode() == KeyCode.W && battleState.isBattleStarted()) {
                    grandPa.addBulletDirection(Direction.UP);
                } else if (event.getCode() == KeyCode.S && battleState.isBattleStarted()) {
                    grandPa.addBulletDirection(Direction.DOWN);
                } else if (event.getCode() == KeyCode.D && battleState.isBattleStarted()) {
                    grandPa.addBulletDirection(Direction.RIGHT);
                } else if (event.getCode() == KeyCode.A && battleState.isBattleStarted()) {
                    grandPa.addBulletDirection(Direction.LEFT);
                }
                else {
                    System.out.println("unused key or battle is busy");
                }
            }
        });
        Platform.runLater(() -> pane.requestFocus());//否则键盘无效
        //完成画布、地图和葫芦娃等的初始化
        canvas.setWidth(CANVAS_WIDTH);//960
        canvas.setHeight(CANVAS_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        battleState = new BattleState();
        Creature.setBattleState(battleState);
        map = new Map(battleState, MAP_REFRESH_RATE, gc, bullets);
        bulletController = new BulletController(map, bullets, battleState);
        scorpion = new Scorpion(map, bullets);
        snake = new Snake(map, bullets);
        Formation.transFormToNextFormation(map, scorpion, snake, evils, bullets);
        initGrandPa();
        initHuluwas();//创建葫芦娃
        map.display(false);
    }

    private void initGrandPa() {
        URL url = this.getClass().getClassLoader().getResource("pictures/grandpa.png");
        Image image = new Image(url.toString());
        grandPa = new GrandPa(map, image, "GrandPa", bullets);
    }

    private void initHuluwas() {
        //葫芦娃的初始化，创建了七个葫芦娃，并且排阵
        for (int i = 1; i <= 7; ++i) {
            URL url = this.getClass().getClassLoader().getResource("pictures/" + i + ".png");
            Image image = new Image(url.toString());
            huluwas.add(new Huluwa(map, image, "h", i, bullets));
        }
        transFormChangShe();
    }

    public void arrangeHuluwas() {
        //将葫芦娃排列为长蛇阵型
        //正常情况下，这是在游戏开始之前执行的，此时只有一个main线程，因此不存在对map的竞争访问
        //葫芦娃都先恢复正常状态:aive,Hp...
        for (Huluwa huluwa : huluwas) {
            huluwa.resetState();
        }
        grandPa.resetState();
        //重新排列
        synchronized (map) {//其实这个synchronized没必要
            transFormChangShe();
        }
    }

    private void transFormChangShe() {
        //将葫芦娃重置为长蛇阵型，需要修改map和huluwa数组,调用Creature的moveTo方法
        int y = NUM_COLUMNS / 4;
        int x = NUM_ROWS / 2 - 3;
        grandPa.moveTo(6, 2);
        for (Huluwa huluwa : huluwas) {
            huluwa.moveTo(x, y);
            ++x;
        }
    }

    public void startGame() {

        try {
            //缓冲一下，加快速度
            //跳出对话框让玩家选择记录文件的保存位置
            FileChooser chooser = new FileChooser();
            chooser.setInitialDirectory(new File("."));
            ;
            chooser.setTitle("保存战斗记录文件");
            //设置选择文件类型
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("游戏记录文件", "*.gamelog*"));
            File file = chooser.showSaveDialog(stage);
            if (file == null) {//如果没有选择，就不能开始游戏
                System.out.println("没有选择保存文件，游戏不能进行");
                return;
            }
            writer = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file.getPath() + ".gamelog")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        map.setWriter(writer);
//        System.out.println("after set:"+writer);
        //按下空格,开始游戏
        battleState.setStarted(true);//状态的结束在map的run线程中设置
        //葫芦娃线程start
        //由于之前是用shutDownNow退出的，如果还是用之前的线程池再execute会出错，因此重新申请一个线程池
        pool = Executors.newCachedThreadPool();
        for (Evil evil : evils) {
            pool.execute(evil);
        }
        for (Huluwa huluwa : huluwas) {
            pool.execute(huluwa);
        }
        pool.execute(grandPa);
        pool.execute(scorpion);
        pool.execute(snake);
        pool.execute(bulletController);//负责子弹移动、出界、伤害

        timeline = new Timeline(//用Timeline 来实现UI的刷新，是javafx安全的
                new KeyFrame(Duration.millis(0),
                        event1 -> {
                            if(!battleState.gamePaused())
                                map.display(true);//每一帧都记录
                            else{
                                //显示暂停画面
                                map.displayPause();
                            }
                        }),
                new KeyFrame(Duration.millis(1000 / MAP_REFRESH_RATE))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        System.out.println("TimeLine play");
        //用一个线程侦听战斗是否结束，while(notEnded) wait(); map的display()检测双方人数，若一方=0，则notifyAll()结束map线程,
        new Thread(() -> {//用lambda表达式代替匿名内部类
            synchronized (battleState) {//观察者模式
                while (battleState.isBattleStarted()) {//等待战斗结束
                    try {
                        battleState.wait();//等待battleState的锁，而不是忙等待监听
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }//等待记录写完
            gameOver();//现在这个侦听线程也要结束了
        }).start();//这个侦听线程不经过pool控制
    }

    public void gameOver() {
        //这个函数在侦听线程中被调用
        //失败一方的所有生物线段都因为alive = false 导致线程退出
        //胜利一方的所有生物线程、map刷新线程、bulletManager线程都可能还在运行
        //因此需要shutDownNow向所有线程发送interrupt()让他们退出
        try {
            writer.close();//确保全部写入
        } catch (IOException e) {
            e.printStackTrace();
        }
        pool.shutdownNow();
        while (((ThreadPoolExecutor) pool).getActiveCount() != 0);
        timeline.stop();//停止显示
        System.out.println("TimeLine stop");
        //现在只剩一个主线程了
        synchronized (map) {
            map.clearMap();
        }
        arrangeHuluwas();
        transFormChangShe();
        bulletController.clearBullets();
        scorpion.resetState();
        snake.resetState();
        grandPa.resetState();
        grandPa.clearMoveDirection();//战斗结束，按键控制序列清空
        grandPa.clearMoveBulletDirection();
        snake.clearDirection();
        Formation.transFormToNextFormation(map, scorpion, snake, evils, bullets);
        //test
        System.out.println(n + "th game over");
        ++n;
        int threadCount = ((ThreadPoolExecutor) pool).getActiveCount();
        System.out.println("线程池中还有 " + threadCount + "个活跃线程");
    }

    public void review() {
        //是在主线程进行的
        //按下L回放
        System.out.println("start review");
        //应该跳出选择框，选择路径
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File("."));
        chooser.setTitle("打开回放记录文件");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("游戏记录文件", "*.gamelog*"));
        File file = chooser.showOpenDialog(stage);
        if (file == null) {//如果没有选择，就不能回放
            System.out.println("没有选择记录文件，回放不能进行");
            return;
        }

        try {
            //装饰器模式
            reader = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
            map.setReader(reader);
            battleState.setReviewing(true);//回放状态的关闭在submit的call线程中
            map.startReview();
            //在review线程内关闭reader
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pauseGame(){
        battleState.setPaused(true);//进入暂停状态
    }

    public void continueGame(){
        battleState.setPaused(false);
        synchronized (battleState){
            battleState.notifyAll();//唤醒暂停的生物、子弹controller
        }
    }
}
