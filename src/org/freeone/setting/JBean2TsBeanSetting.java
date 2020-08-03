package org.freeone.setting;


import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.fields.valueEditors.ValueEditor;

import javax.swing.JTable;
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
public class JBean2TsBeanSetting implements Configurable,Configurable.Composite {
    private JPanel contentPane;

    private JTable pathTable;

    private JScrollPane scrollPane;

    private JBean2TsBeanComponent settings = JBean2TsBeanComponent.getInstance();

    public JBean2TsBeanSetting(){init();}

    private void init() {
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);

        JButton addButton = new JButton("增加映射");
        addButton.setBounds(10, 10, 93, 23);
        addButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
               /* DefaultTableModel defaultTableModel = (DefaultTableModel)pathTable.getModel();
                FileChooserDescriptor javaOriginFolderDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
                javaOriginFolderDescriptor.setTitle("选择java源路径");
                VirtualFile chooseFile = FileChooser.chooseFile(javaOriginFolderDescriptor, null, null);
                String javaOriginFolder = null;
                String typeScriptTargetFolder = null;
                if (chooseFile == null){
                    Messages.showErrorDialog("请选择Java源路径", "错误");
                    return;
                }
                javaOriginFolder = chooseFile.getPath();
                FileChooserDescriptor typescriptTargetFolderDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
                typescriptTargetFolderDescriptor.setTitle("选择TypeScript目标路径");
                chooseFile = FileChooser.chooseFile(typescriptTargetFolderDescriptor, null, null);
                if (chooseFile == null){
                    Messages.showErrorDialog("请选择TypeScript目标路径", "错误");
                    return;
                }
                typeScriptTargetFolder = chooseFile.getPath();
                System.err.println(javaOriginFolder+" : " + typeScriptTargetFolder);
                Vector vCells = new Vector();
                Vector vRows = new Vector();
                // 字段
                Vector<String> columns = new Vector<>();
                for (int i = 0; i < defaultTableModel.getColumnCount(); i++) {
                    columns.add(defaultTableModel.getColumnName(i));
                }
                Vector dataVector = defaultTableModel.getDataVector();
                vCells.add(dataVector.size()+"."+javaOriginFolder);
                vCells.add(typeScriptTargetFolder);
                dataVector.add(vCells);
                defaultTableModel = new DefaultTableModel(dataVector, columns);*/
                System.err.println(123);
                pathTable.setModel(new DefaultTableModel(
                        new Object[][] {
                                {2, 2},
                                {12, 2}

                        },
                        new String[] {
                                "Java\u6E90\u8DEF\u5F84", "TypeScript\u76EE\u6807\u8DEF\u5F84"
                        }
                ));
                pathTable.updateUI();
                contentPane.repaint();


            }
        });
        contentPane.add(addButton);

        JButton deleteButton = new JButton("删除映射");
        deleteButton.setBounds(109, 10, 93, 23);
        contentPane.add(deleteButton);

        pathTable = new JTable();
        pathTable.setModel(new DefaultTableModel(
                new Object[][] {
                        {1, 2}
                },
                new String[] {
                        "Java源路径", "TypeScript目标路径"
                }
        ));
        pathTable.setBounds(0, 0, 700, 180);
        pathTable.setVisible(false);

         scrollPane = new com.intellij.ui.components.JBScrollPane(pathTable);
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
