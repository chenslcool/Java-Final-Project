package creature;

import battle.Map;
import bullet.Bullet;
import bullet.HorizontalBulletGenerator;
import creature.enumeration.Camp;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.LinkedList;


/**
 * @author csl
 * @date 2019/11/25 12:41
 */
public class Evil extends Creature{//妖精是在BattleControler中创建的
    public Evil(Map map, LinkedList<Bullet> bullets){
        super(map,bullets);
        //设置image
        URL url = this.getClass().getClassLoader().getResource("pictures/" + "lolo.png");
        this.image = new Image(url.toString());//这样很麻烦，最好能抽象出一个Image类，保存所有图片，以后的
        //构造器就直接用
        this.camp = Camp.EVIL;
        this.attackValue = EVIL_ATK;
        this.defenseValue = EVIL_DEF;
        bulletBulletGenerator = new HorizontalBulletGenerator();
    }

    @Override
    public String getSimpleName(){
        return "Evil";
    }


}
