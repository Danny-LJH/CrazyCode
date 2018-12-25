package com.oahzuw;

import com.oahzuw.utils.ScreenShot;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.awt.*;

/**
 * @author: 武钊 <oahzuw@live.com>
 * @date: 2018/12/25 17:47
 * @description:
 */
public class Controller {
    @FXML
    private TextArea text;

    @FXML
    public void getSc() {
        try {
            ScreenShot ssw = new ScreenShot(text);
            ssw.setVisible(true);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void clear() {
        text.clear();
    }
}
