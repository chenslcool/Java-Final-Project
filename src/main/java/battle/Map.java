package battle;

import creature.Creature;

/**
 * @author csl
 * @date 2019/11/24 20:56
 */
public class Map implements Runnable,Config {
    private Creature[][] grounds;
    private int refreshRate;//刷新频率
    private BattleState battleState;
    public Map(BattleState battleState,int refreshRate){
        grounds = new Creature[NUM_ROWS][NUM_COLUMNS];//初始化为NULL
        this.battleState = battleState;
        this.refreshRate = refreshRate;
    }
    public Creature getCreatureAt(int x,int y){
        return grounds[x][y];
    }
    public void setCreatureAt(int x,int y,Creature creature){
        if(grounds[x][y] != null){
            grounds[x][y] = creature;
        }
    }
    public boolean noCreatureAt(int x,int y){
        return grounds[x][y] == null;
    }
    public void display(){
        //TODO draw battlefield on canvas using conetext

    }
    public void run() {
        //TODO update canvaas using display() accordin to given hz
        //如果战斗结束或者暂停或结束就退出run(),结束线程
        while(battleState.battleEnded == false && battleState.battlePaused ==false){
            display();
        }
    }

    public boolean insideMap(int x,int y){//坐标是否在地图内
        if(x < 0 || x >= NUM_ROWS || y < 0|| y>= NUM_COLUMNS)
            return false;
        else
            return true;
    }
}
