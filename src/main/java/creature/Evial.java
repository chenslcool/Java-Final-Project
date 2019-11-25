package creature;

import battle.Map;
import javafx.scene.image.Image;

import java.net.URL;


/**
 * @author csl
 * @date 2019/11/25 12:41
 */
public class Evial extends Creature{//妖精是在BattleControler中创建的
    public Evial(Map map){
        super(map);
        //设置image
        URL url = this.getClass().getClassLoader().getResource("pictures/" + "lolo.jpg");
        this.image = new Image(url.toString());//这样很麻烦，最好能抽象出一个Image类，保存所有图片，以后的
        //构造器就直接用
    }
    @Override
    public void attack() {
        //TODO 添加攻击行为
    }


}
