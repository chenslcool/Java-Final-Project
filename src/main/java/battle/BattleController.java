package battle;

import creature.Huluwa;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.ArrayList;

/**
 * @author csl
 * @date 2019/11/24 21:47
 */
public class BattleController {
    int refreshRate = 10;
    ArrayList<Huluwa> huluwas;
    @FXML
    private Canvas canvas;//在构造函数里面canvas还是null,我也不知道什么时候初始化完成的
    Map map;//地图
    BattleState battleState;
    public BattleController(){
        huluwas = new ArrayList<Huluwa>();
        battleState = new BattleState();
        map = new Map(battleState,refreshRate);
        initHuluwas();
    }
    public void initHuluwas(){
        //初始化七个葫芦娃
        for(int i = 1;i <= 7;++i){
            URL url = this.getClass().getClassLoader().getResource("pictures/"+i+".jpg");
            Image image = new Image(url.toString());
            huluwas.add(new Huluwa(map,image,"h"));
        }
    }



}
