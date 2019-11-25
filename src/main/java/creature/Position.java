package creature;

/**
 * @author csl
 * @date 2019/11/24 20:49
 */
public class Position {
    private int x = -1;//无效
    private int y = -1;
    public void setX(int x){
        this.x = x;
    }
    public void setY(int y){
        this.y = y;
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
}
