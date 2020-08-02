package org.freeone.window;


import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author wengyongcheng
 * @since 2020/3/1 10:30 下午
 */
public class MyToolWindow  {

    private JButton hideButton;

    private JLabel datetimeLabel;

    private JScrollPane myToolWindowContent;

    public MyToolWindow(ToolWindow toolWindow) {

        init();

        hideButton.addActionListener(e -> toolWindow.hide(null));
    }

    private void init() {
        datetimeLabel = new JLabel();
        datetimeLabel.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        hideButton = new JButton("取消");


        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        textArea.setEditable(true);
        textArea.setRows(8);
        textArea.setLineWrap(true);
        textArea.setForeground(Color.BLACK);
        textArea.setBackground(Color.WHITE);

        myToolWindowContent = new JScrollPane(textArea);
//        myToolWindowContent.add(datetimeLabel);
//        myToolWindowContent.add(hideButton);

    }

    public JScrollPane getContent() {
        return myToolWindowContent;
    }

}