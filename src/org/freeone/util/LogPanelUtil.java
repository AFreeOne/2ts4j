package org.freeone.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;

import javax.swing.*;

/**
 * 下方日志面板的相关类
 */
public class LogPanelUtil {

    /**
     * 获取日志窗口的textarea
     * @param project 当前项目
     * @return  JTextArea
     */
    public JTextArea getTextArea(Project project){
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        ToolWindow java_bean_to_ts_bean_result = (ToolWindow)toolWindowManager.getToolWindow("java bean to ts bean result");
        boolean visible = java_bean_to_ts_bean_result.isVisible();
        if (!visible){
            java_bean_to_ts_bean_result.show(null);
        }
        JComponent component = java_bean_to_ts_bean_result.getComponent();
        ContentManager contentManager = java_bean_to_ts_bean_result.getContentManager();
        Content[] contents = contentManager.getContents();
        JScrollPane jScrollPane = (JScrollPane)contents[0].getComponent();
        return (JTextArea)jScrollPane.getViewport().getComponent(0);
    }

    /**
     * 清理日志信息
     * @param project 当前项目
     */
    public void clearTextArea(Project project){
        try {
            JTextArea textArea = getTextArea(project);
            textArea.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 日志窗口输出转换完成
     * @param project 当前项目
     */
    public void writeActionComplete(Project project){
        JTextArea textArea = getTextArea(project);
        textArea.append("\n");
        textArea.append("========== 转 换 完 成 ==========");
        textArea.append("\n");
    }

    /**
     * 将info添加到面板上
     * @param project 当前项目
     * @param info 字符串
     */
    public void writeInfo(Project project,String info){
        try {
            JTextArea textArea = getTextArea(project);
            textArea.append("\n");
            textArea.append("> " + info);
            textArea.append("\n");
            textArea.setCaretPosition(textArea.getText().length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
