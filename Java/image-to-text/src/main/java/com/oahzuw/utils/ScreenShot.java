package com.oahzuw.utils;

import com.alibaba.fastjson.JSON;
import javafx.scene.control.TextArea;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author: 武钊 <oahzuw@live.com>
 * @date: 2018/12/25 17:47
 * @description:
 */
public class ScreenShot extends JWindow {
    /**
     * 鼠标坐标相关
     */
    private int orgx, orgy, endx, endy;
    /**
     * 截图相关
     */
    private BufferedImage image = null;
    private BufferedImage tempImage = null;
    private BufferedImage saveImage = null;


    public ScreenShot(TextArea text) throws AWTException {
        //获取屏幕尺寸
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds(0, 0, d.width, d.height);

        //截取屏幕
        Robot robot = new Robot();
        image = robot.createScreenCapture(new Rectangle(0, 0, d.width, d.height));

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // 鼠标松开时记录结束点坐标，并隐藏操作窗口
                orgx = e.getX();
                orgy = e.getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // 鼠标松开后, 关闭全屏遮罩
                ScreenShot.this.setVisible(false);
                try {
                    // 把BufferedImage 转为 byte[]
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    ImageIO.write(saveImage, "jpg", out);

                    // 调用百度图片识别api
                    JSONObject res = BaiDuAiUtils.getClient().basicGeneral(out.toByteArray(), null);
                    // 获取识别的结果
                    JSONArray words_result = res.getJSONArray("words_result");
                    // 可能有多个结果, 遍历组装
                    String temp = "";
                    for (Object obj : words_result) {
                        temp += JSON.parseObject(obj.toString()).getString("words") + "\r\n";
                    }
                    text.appendText(temp);

                    // 设置到系统剪切版
                    setSysClipboardText(temp);

                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                // 每次识别都换行
                text.appendText("\n");
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                //鼠标拖动时，记录坐标并重绘窗口
                endx = e.getX();
                endy = e.getY();

                //临时图像，用于缓冲屏幕区域放置屏幕闪烁
                Image tempImage2 = createImage(ScreenShot.this.getWidth(), ScreenShot.this.getHeight());
                Graphics g = tempImage2.getGraphics();
                g.drawImage(tempImage, 0, 0, null);
                int x = Math.min(orgx, endx);
                int y = Math.min(orgy, endy);
                int width = Math.abs(endx - orgx) + 1;
                int height = Math.abs(endy - orgy) + 1;
                // 加上1防止width或height0
                g.setColor(Color.BLUE);
                g.drawRect(x - 1, y - 1, width + 1, height + 1);
                //减1加1都了防止图片矩形框覆盖掉
                saveImage = image.getSubimage(x, y, width, height);
                g.drawImage(saveImage, x, y, null);
                ScreenShot.this.getGraphics().drawImage(tempImage2, 0, 0, ScreenShot.this);
            }
        });
    }

    /**
     * 图像填充
     *
     * @param g
     */
    @Override
    public void paint(Graphics g) {
        RescaleOp ro = new RescaleOp(0.8f, 0, null);
        tempImage = ro.filter(image, null);
        g.drawImage(tempImage, 0, 0, this);
    }


    /**
     * 设置文本到剪切板。
     */
    private static void setSysClipboardText(String msg) {
        Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
        sysClip.setContents(new StringSelection(msg), null);
    }
}
