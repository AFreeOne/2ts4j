package org.freeone.setting;


import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.fields.valueEditors.ValueEditor;


import com.intellij.ui.table.JBTable;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

/**
 * https://www.jianshu.com/p/e6d49e3c7c60
 * 2、创建GUI Form对象MainSetting，作为设置主入口
 * @author wengyongcheng
 * @since 2020/3/14 10:16 下午
 */
@Deprecated
public class JBean2TsBeanSetting implements Configurable,Configurable.Composite {
     JBPanel contentPane;

     JTable pathTable;

    JButton addButton;

     JBScrollPane scrollPane;

     JBean2TsBeanComponent settings = JBean2TsBeanComponent.getInstance();

     JBean2TsBeanSetting(){init();}

    private void init() {
        contentPane = new JBPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);

        addButton = new JButton("增加映射");
        addButton.setBounds(10, 10, 93, 23);

        contentPane.add(addButton);


        JButton deleteButton = new JButton("删除映射");
        deleteButton.setBounds(109, 10, 93, 23);
        contentPane.add(deleteButton);

        pathTable = new JBTable();

        pathTable.setModel(new DefaultTableModel(
                new Object[][] {
                        {"123", "123222"}
                },
                new String[] {
                        "Java源路径", "TypeScript目标路径"
                }
        ));

        pathTable.setBounds(0, 0, 700, 180);
        scrollPane = new JBScrollPane(pathTable);
        scrollPane.setBounds(10, 43, 714, 191);
        contentPane.add(scrollPane);







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
        return contentPane;
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

        addButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                pathTable.setModel(new DefaultTableModel(
                        new Object[][] {
                                {"11231231231231231231312323", "123222"}
                        },
                        new String[] {
                                "Java源路径11", "TypeScript目标路径"
                        }
                ));
                pathTable.updateUI();

            }
        });

        return true;
    }

    /**
     * 点击apply按钮后被调用
     * @throws ConfigurationException
     */
    @Override
    public void apply() throws ConfigurationException {

    }

    /**
     * reset按钮被点击时触发
     */
    @Override
    public void reset() {
        init();
    }


}
