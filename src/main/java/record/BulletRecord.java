package record;

import annotations.Info;
import javafx.scene.paint.Color;

import java.io.Serializable;

/**
 * @author csl
 * @date 2019/12/1 20:27
 */
@Info(description = "record one bullet on court")
public class BulletRecord implements Serializable {
    public double x;
    public double y;
    //    public Color color; color不能被序列化
    public boolean isRed;

    public BulletRecord(double x, double y, Color color) {
        this.x = x;
        this.y = y;
        this.isRed = (color.equals(Color.DEEPPINK));//用等号和equals都行，等号是比较是否同一个引用，重载了equals比较rgb
    }
}
