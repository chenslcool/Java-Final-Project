package creature;

import creature.enumeration.Direction;

/**
 * @author csl
 * @date 2019/12/19 23:45
 */
public interface Controllable {
    void setControlled(boolean controlled);
    void controlMove();//按照控制方式移动
    void controlAttack();//按照控制攻击
    void addMoveDirection(Direction direction);//往攻击列表加入控制方向
    void addAttackDirection(Direction direction);
    void clearMoveDirection();
    void clearAttackDirection();
}
