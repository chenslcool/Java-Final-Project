package battle;

import bullet.Bullet;
import creature.Creature;
import creature.Curable;
import creature.enumeration.Camp;
import javafx.scene.ImageCursor;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import record.BulletRecord;
import record.CreatureRecord;
import record.Record;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author csl
 * @date 2019/11/24 20:56
 */
public class Map implements Runnable, Config, Callable<String> {
    private Creature[][] grounds;//这个的序列化
    private int refreshRate;//刷新频率
    private GraphicsContext gc;//通过gc直接绘图
    private BattleState battleState;
    private LinkedList<Bullet> bullets;//display的时候也要显示子弹，这个的序列化很容易
    private Image deadImage;
    private Image backGroundImage;
    private ObjectOutputStream writer;
    private ObjectInputStream reader;
    private HashMap<String, Image> typeImageMap;//类型名称->image的字典
    public Map(BattleState battleState, int refreshRate, GraphicsContext gc, LinkedList<Bullet> bullets) {
        grounds = new Creature[NUM_ROWS][NUM_COLUMNS];//初始化为NULL
        this.battleState = battleState;
        this.refreshRate = refreshRate;
        this.gc = gc;
        this.bullets = bullets;
        URL url = this.getClass().getClassLoader().getResource("pictures/" + "ghost.png");
        deadImage = new Image(url.toString());
        url = this.getClass().getClassLoader().getResource("pictures/" + "background.jpg");
        backGroundImage = new Image(url.toString());
        this.writer = writer;
        initDictionary();
    }

    private void initDictionary(){
        typeImageMap = new HashMap<>();
        URL url = this.getClass().getClassLoader().getResource("pictures/" + "ghost.png");
        typeImageMap.put("Dead",new Image(url.toString()));
        //葫芦娃
        for(int i = 1;i <= 7;++i){
            url = this.getClass().getClassLoader().getResource("pictures/" + i + ".png");
            typeImageMap.put("Huluwa"+i,new Image(url.toString()));
        }
        //老爷爷
        url = this.getClass().getClassLoader().getResource("pictures/" + "grandpa.png");
        typeImageMap.put("GrandPa",new Image(url.toString()));
        url = this.getClass().getClassLoader().getResource("pictures/" + "scorpion.png");
        typeImageMap.put("Scorpion",new Image(url.toString()));
        url = this.getClass().getClassLoader().getResource("pictures/" + "snake.png");
        typeImageMap.put("Snake",new Image(url.toString()));
        url = this.getClass().getClassLoader().getResource("pictures/" + "lolo.png");
        typeImageMap.put("Evil",new Image(url.toString()));
    }

    public Creature getCreatureAt(int x, int y) {
        if(insideMap(x,y) == false){
            return null;
        }
        else
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

    public void display(boolean needRecord) {//是否需要记录
        Record record = new Record();
        //双方剩余人数
        int numJusticeLeft = 0;
        int numEvilLeft = 0;
        //先清空画布
//        gc.clearRect(0,0,CANVAS_WIDTH,CANVAS_HEIGHT); //好像清空画布是没必要的，整个覆盖了
        //画背景
        gc.drawImage(backGroundImage,0,0,CANVAS_WIDTH,CANVAS_HEIGHT);
        //先画12*16的网格
        drawBoardLines();
        //绘制所有Creature
        synchronized (this){//锁住地图
            for(int i = 0;i<NUM_ROWS;++i){
                for(int j = 0;j<NUM_COLUMNS;++j){
                    Creature c = this.getCreatureAt(i,j);
                    if(c != null){
                        synchronized (c){//画生物的时候它不能被攻击、移动
                            record.creatureRecords.add(new CreatureRecord(i,j,c.getCamp(),c.getCurrentHP(),c.isAlive(),c.getSimpleName()));
                            if(c.isAlive())
                            {
                                if(c.getCamp()== Camp.JUSTICE){
                                    numJusticeLeft ++;
                                }
                                else
                                    numEvilLeft ++;
                                gc.drawImage(c.getImage(),j*UNIT_SIZE,i*UNIT_SIZE,UNIT_SIZE-1,UNIT_SIZE-1);
                                //画血量
                                int currentHp = c.getCurrentHP();
                                int maxHp = c.getMAX_HP();
                                int greenLen = UNIT_SIZE*currentHp/maxHp;
//                                double redLen = UNIT_SIZE - greenLen;
                                gc.setLineWidth(BLOOD_LINE_WIDTH);
                                gc.setStroke(Color.LIGHTGREEN);
                                gc.strokeLine(j*UNIT_SIZE,i*UNIT_SIZE,j*UNIT_SIZE + greenLen,i*UNIT_SIZE);
                                if(currentHp != maxHp)
                                {
                                    gc.setStroke(Color.RED);
                                    gc.strokeLine(j*UNIT_SIZE + greenLen,i*UNIT_SIZE,(j+1)*UNIT_SIZE,i*UNIT_SIZE);
                                }
                                if(c instanceof Curable){
                                    //画治愈绿色,会不会挡住其他生物，其实这样的画法不太行，在边缘的时候不对
                                    //设置透明度
                                    gc.setFill(Color.rgb(0,255,0,0.3));
                                    double x1=((j-1)>0?j-1:0)*UNIT_SIZE;
                                    double y1=((i-1)>0?i-1:0)*UNIT_SIZE;
                                    gc.fillRect(x1,y1,3*UNIT_SIZE,3*UNIT_SIZE);
                                }
                            }
                            else{
                                gc.drawImage(deadImage,j*UNIT_SIZE,i*UNIT_SIZE,UNIT_SIZE-1,UNIT_SIZE-1);
                            }
                        }
                    }
                }
            }
        }
        //绘制所有的子弹
        synchronized (bullets){//锁住
            for(Bullet bullet:bullets){
                record.bulletRecords.add(new BulletRecord(bullet.getX(),bullet.getY(),bullet.getColor()));
                gc.setFill(bullet.getColor());
                gc.fillOval(bullet.getY(),bullet.getX(),BULLTE_RADIUS,BULLTE_RADIUS);
            }
        }

        //将这一帧写入文件
        if(needRecord) {
            try {
                writer.writeObject(record);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(numEvilLeft ==0 || numJusticeLeft == 0){
            battleState.setStarted(false);
            if(numEvilLeft == 0){//设置战斗胜利者
                battleState.setWinner(Camp.JUSTICE);
            }
            else{
                battleState.setWinner(Camp.EVIL);
            }
            synchronized (battleState){
                battleState.notifyAll();//唤醒侦听线程
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
        //序列化输出writer的文件打开操作是在主线程执行的
        //如果战斗结束或者暂停或结束就退出run(),结束线程
        while (battleState.isBattleStarted() && Thread.interrupted() == false) {
            try {
                TimeUnit.MILLISECONDS.sleep(1000/refreshRate);
                display(true);//display内部上锁顺序: map -> creature
//                record();//记录一帧
            } catch (InterruptedException e) {
                break;
            }
        }
        //关闭序列化输出在本线程执行，如果在主线程关闭的画，本线程还未结束，可能继续用已经closed的writer
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("map.run() exit");
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

    public void clearMap(){
        //清空map
        for(int i = 0;i < NUM_ROWS;++i){
            for(int j =0;j < NUM_COLUMNS;++j){
                grounds[i][j] = null;
            }
        }
    }

    public void setWriter(ObjectOutputStream writer){
        this.writer = writer;
    }

    @Override
    public String call(){
        review(reader);
        battleState.setReviewing(false);//回放结束
        return "review";
    }

    public void setReader(ObjectInputStream reader){
        this.reader = reader;
    }

    public void review(ObjectInputStream reader){
        //review一定只有单线程
        //为什么作为callable，放到call中调用review就可以啊!!!
        while (true){
            try {
               Record record = (Record)reader.readObject();
               drawRecord(record);
               TimeUnit.MILLISECONDS.sleep(1000/refreshRate);
            } catch (EOFException e) {//正常结束
                try {
                    reader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //画一帧记录
    public void drawRecord(Record record){
        //先清空画布
        gc.clearRect(0,0,CANVAS_WIDTH,CANVAS_HEIGHT);
        //画背景
        gc.drawImage(backGroundImage,0,0,CANVAS_WIDTH,CANVAS_HEIGHT);
        //先画12*16的网格
        drawBoardLines();

        ArrayList<CreatureRecord> creatureRecords = record.creatureRecords;
        ArrayList<BulletRecord> bulletRecords = record.bulletRecords;
        //先画生物
        for(CreatureRecord r:creatureRecords){
            //TODO 根据type从type-image map中选出图片
            Image image = typeImageMap.get(r.type);
            if(r.alive)
            {
//                System.out.println("inside");
                gc.drawImage(image,r.y*UNIT_SIZE,r.x*UNIT_SIZE,UNIT_SIZE-1,UNIT_SIZE-1);
                //画血量
                int greenLen = UNIT_SIZE*r.currentHP/DEFAULT_MAX_HP;
//                                double redLen = UNIT_SIZE - greenLen;
                gc.setLineWidth(BLOOD_LINE_WIDTH);
                gc.setStroke(Color.LIGHTGREEN);
                gc.strokeLine(r.y*UNIT_SIZE,r.x*UNIT_SIZE,r.y*UNIT_SIZE + greenLen,r.x*UNIT_SIZE);
                if(r.currentHP != DEFAULT_MAX_HP)
                {
//                    System.out.println("not full");
                    gc.setStroke(Color.RED);
                    gc.strokeLine(r.y*UNIT_SIZE + greenLen,r.x*UNIT_SIZE,(r.y+1)*UNIT_SIZE,r.x*UNIT_SIZE);
                }
                //TODO 处理curable的情况???
            }
            else{
                gc.drawImage(deadImage,r.y*UNIT_SIZE,r.x*UNIT_SIZE,UNIT_SIZE-1,UNIT_SIZE-1);
            }
        }

        for(BulletRecord r:bulletRecords){
            Color color = r.isRed? Color.DEEPPINK:Color.LIGHTGREEN;
            gc.setFill(color);
            gc.fillOval(r.y,r.x,BULLTE_RADIUS,BULLTE_RADIUS);
        }
    }
}
