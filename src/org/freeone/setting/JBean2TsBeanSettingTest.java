package org.freeone.setting;


import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import org.freeone.swing.JBeantoTsBeanFolderMapTablePanel;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * https://www.jianshu.com/p/e6d49e3c7c60
 * 2、创建GUI Form对象MainSetting，作为设置主入口
 * @author wengyongcheng
 * @since 2020/3/14 10:16 下午
 */
public class JBean2TsBeanSettingTest implements Configurable,Configurable.Composite {
    private JBPanel contentPane;

    private JPanel folderMapPanel;

    private JTable pathTable;

    private JBScrollPane scrollPane;

    private JBean2TsBeanComponent settings = JBean2TsBeanComponent.getInstance();

    public JBean2TsBeanSettingTest(){init();}

    private void init() {
        this.folderMapPanel =  new JBeantoTsBeanFolderMapTablePanel().folderMapPanel;
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
//        String origSetting = settings.getSettingMap().get("folder path");
//        String newSetting = textField.getText();
//        return !StringUtils.equals(origSetting,newSetting);
        return true;
    }

    /**
     * 点击apply按钮后被调用
     * @throws ConfigurationException
     */
    @Override
    public void apply() throws ConfigurationException {
        settings.getSettingMap().put("folder path", "asd");
        settings.getFolderList().add("1,1");
    }

    /**
     * reset按钮被点击时触发
     */
    @Override
    public void reset() {
        init();
    }


}
