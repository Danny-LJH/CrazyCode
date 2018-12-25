package com.oahzuw;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * @author: 武钊 <oahzuw@live.com>
 * @date: 2018/12/25 17:46
 * @description:
 */
public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Controller.fxml"));
        // 设置标题
        primaryStage.setTitle("Java图片文本识别");
        // 禁止改变窗口大小
        primaryStage.setResizable(false);
        // 设置窗口大小
        primaryStage.setScene(new Scene(root, 600, 480));
        TextArea textArea = (TextArea) root.lookup("#text");
        // 禁止编辑文本域
        textArea.setEditable(false);
        // 显示窗口
        primaryStage.show();

        // 截图窗口会一直存在, 导致不点击关闭不能退出程序, 手动退出虚拟机
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
    }
}
