package record;

import javafx.scene.paint.Color;
import java.io.Serializable;

/**
 * @author csl
 * @date 2019/12/1 20:27
 */
public class BulletRecord implements Serializable {
    public double x;
    public double y;
//    public Color color; color不能被序列化
    public boolean isRed;
    public BulletRecord(double x,double y ,Color color){
        this.x = x;
        this.y = y;
        this.isRed = (color == Color.RED);//这样可以吗,应该可以，内部对象
    }
}
