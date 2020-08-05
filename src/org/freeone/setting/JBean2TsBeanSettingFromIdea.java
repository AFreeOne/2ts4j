package org.freeone.setting;


import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import org.freeone.swing.JBeantoTsBeanFolderMapTablePanel;
import org.freeone.util.InstanceUtil;
import org.freeone.util.TemplateUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * https://www.jianshu.com/p/e6d49e3c7c60
 * 2、创建GUI Form对象MainSetting，作为设置主入口
 * @author wengyongcheng
 * @since 2020/3/14 10:16 下午
 */
public class JBean2TsBeanSettingFromIdea implements Configurable,Configurable.Composite {

    private JBPanel contentPane;

    private JPanel folderMapPanel;

    private JTable pathTable;

    private JBScrollPane scrollPane;

    private JBean2TsBeanComponent settings = JBean2TsBeanComponent.getInstance();

    public JBean2TsBeanSettingFromIdea(){init();}

    private void init() {
        JBeantoTsBeanFolderMapTablePanel jBeanToTsBeanFolderMapTablePanelInstance = InstanceUtil.getJBeantoTsBeanFolderMapTablePanelInstance();
        this.folderMapPanel = jBeanToTsBeanFolderMapTablePanelInstance.folderMapPanel;
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "JBean 2 Ts Bean Setting";
    }

    /**
     * 通过方法返回定义的子设置组件
     * @return
     */
    @NotNull
    @Override
    public Configurable[] getConfigurables() {
        Configurable[] configurables = new Configurable[1];
        configurables[0] = new SubSetting();
        return configurables;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return folderMapPanel;
    }

    /**
     * 设置apply按钮是否可用，
     * @return
     */
    @Override
    public boolean isModified() {

        return true;
    }

    /**
     * 点击apply按钮后被调用
     * @throws ConfigurationException
     */
    @Override
    public void apply() throws ConfigurationException {

        // TODO 点击apply保存表格数据
        Component[] components = folderMapPanel.getComponents();
        JScrollPane jScrollPane = null;
        for (int i = 0; i < components.length; i++) {
            if(components[i] instanceof JScrollPane){
                jScrollPane = (JScrollPane)components[i];
                break;
            }
        }
        if (jScrollPane == null){
            Messages.showErrorDialog("无法获取设置中的滚动面板", "错误");
            return;
        }

        JBTable table = (JBTable)jScrollPane.getViewport().getView();
        int rowCount = table.getRowCount();
        List<String> folderMappingList = new ArrayList<>();
        Vector rowDate = new Vector();
        for (int i = 0; i < rowCount; i++) {

            Object javaPath = table.getValueAt(i, 0);
            Object tsPath = table.getValueAt(i, 1);
            String folderMapping = (String) javaPath+"|" + (String) tsPath;
            folderMappingList.add(folderMapping);

        }
        settings.setFolderMappingList(folderMappingList);
    }

    /**
     * reset按钮被点击时触发
     */
    @Override
    public void reset() {
        reloadTable();
    }


    @Override
    public void cancel() {
        reloadTable();
    }

    /**
     * 重新加载表格
     */
    private void reloadTable(){
        Component[] components = folderMapPanel.getComponents();
        JScrollPane jScrollPane = null;
        for (int i = 0; i < components.length; i++) {
            if(components[i] instanceof JScrollPane){
                jScrollPane = (JScrollPane)components[i];
                break;
            }
        }
        if (jScrollPane == null){
            Messages.showErrorDialog("无法获取设置中的滚动面板", "错误");
            return;
        }
        JBTable table = (JBTable)jScrollPane.getViewport().getView();
        Vector rowDate = new Vector();
        List<String> folderMappingList = settings.getFolderMappingList();
        for (int i = 0; i < folderMappingList.size(); i++) {
            Vector cellDate = new Vector();
            String folderMapping = folderMappingList.get(i);
            String[] folders = folderMapping.split(TemplateUtil.FOLDER_SPLIT);
            String originFolder = folders[0];
            String targetFolder = folders[1];
            cellDate.add(originFolder);
            cellDate.add(targetFolder);
            rowDate.add(cellDate);
        }

        Vector columnData = new Vector();
        columnData.add("Java源路径");
        columnData.add("TypeScript目标路径");
        table.setModel(new DefaultTableModel(rowDate, columnData));
    }



}
