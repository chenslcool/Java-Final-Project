package record;

import annotations.Info;
import creature.enumeration.Camp;
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
    public Camp camp;

    public BulletRecord(double x, double y, Camp camp) {
        this.x = x;
        this.y = y;
        this.camp = camp;
    }
}
