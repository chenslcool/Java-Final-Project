/**
 * @author csl
 * @date 2019/11/24 14:53
 */

import battle.BattleController;
import battle.Config;
import creature.enumeration.Camp;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application implements Config {

    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("battle.fxml"));
        primaryStage.setTitle("空格开始游戏,L复盘回放,F改变阵型,方向键控制移动,WSAD发射子弹");
        primaryStage.setScene(new Scene(root, CANVAS_WIDTH, CANVAS_HEIGHT)); //设置初始的窗口大小
        primaryStage.show();
        primaryStage.setResizable(false);//不可变更窗口大小
        BattleController.stage = primaryStage;
        //监听窗口关闭事件，回收线程
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.out.print("窗口关闭");
                System.exit(0);
            }
        });

    }

    public static void main(String[] args) {
        launch(args);
    }
}
