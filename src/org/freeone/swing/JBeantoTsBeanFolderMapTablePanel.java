package org.freeone.swing;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.table.JBTable;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.freeone.setting.JBean2TsBeanComponent;
import org.freeone.util.TemplateUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;


public class JBeantoTsBeanFolderMapTablePanel {

    public JPanel folderMapPanel;
    private JButton addButton;
    private JButton removeButton;
    private JTable table1;
    private JScrollPane jScrollPane;


    public JBeantoTsBeanFolderMapTablePanel() {

        $$$setupUI$$$();
        addButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                FileChooserDescriptor javaFolderDescriptor = TemplateUtil.createFileChooserDescriptor("请选择java源路径", null);
                VirtualFile javaFileChoose = FileChooser.chooseFile(javaFolderDescriptor, null, null);
                if (javaFileChoose == null) {
                    Messages.showErrorDialog("请选择Java源路径", "错误");
                    return;
                }
                String javaFileChoosePath = javaFileChoose.getPath();
                // 获取表格的数据
                Vector rowDate = new Vector();
                int rowCount = table1.getRowCount();
                for (int i = 0; i < rowCount; i++) {
                    Vector cellDate = new Vector();
                    String cellJavaPath = (String) table1.getValueAt(i, 0);
                    if (javaFileChoosePath.equals(cellJavaPath)) {
                        Messages.showErrorDialog("Java源路径已经存在", "错误");
                        return;
                    }
                    cellDate.add(cellJavaPath);
                    cellDate.add(table1.getValueAt(i, 1));
                    rowDate.add(cellDate);
                }


                FileChooserDescriptor typeScriptDescriptor = TemplateUtil.createFileChooserDescriptor("请选择TypeScript目标路径", null);
                VirtualFile typeScriptFileChoose = FileChooser.chooseFile(typeScriptDescriptor, null, null);
                if (typeScriptFileChoose == null) {
                    Messages.showErrorDialog("请选择TypeScript目标路径", "错误");
                    return;
                }

                String typeScriptFileChoosePath = typeScriptFileChoose.getPath();

                JBean2TsBeanComponent instance = JBean2TsBeanComponent.getInstance();
                LinkedHashMap<String, String> folderMap = instance.getFolderMap();


                Vector cellDate = new Vector();
                cellDate.add(javaFileChoosePath);
                cellDate.add(typeScriptFileChoosePath);
                rowDate.add(cellDate);

                Vector columnData = new Vector();
                columnData.add("Java源路径");
                columnData.add("TypeScript目标路径");
                table1.setModel(new DefaultTableModel(rowDate, columnData));


            }
        });
        removeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int[] selectedRows = table1.getSelectedRows();
                if (selectedRows == null || selectedRows.length == 0) {
                    Messages.showErrorDialog("请选择行数据", "错误");
                    return;
                }
                List<Integer> selectedRowList = new ArrayList<>();
                for (int selectedRow : selectedRows) {
                    selectedRowList.add(selectedRow);
                }

                Vector rowDate = new Vector();
                int rowCount = table1.getRowCount();
                for (int i = 0; i < rowCount; i++) {
                    if (selectedRowList.indexOf(i) != -1) {
                        // 如果在选中行中，就跳过
                        continue;
                    }
                    Vector cellDate = new Vector();
                    String cellJavaPath = (String) table1.getValueAt(i, 0);
                    cellDate.add(cellJavaPath);
                    cellDate.add(table1.getValueAt(i, 1));
                    rowDate.add(cellDate);
                }
                Vector columnData = new Vector();
                columnData.add("Java源路径");
                columnData.add("TypeScript目标路径");
                table1.setModel(new DefaultTableModel(rowDate, columnData));


            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("JBeantoTsBeanFolderMapTablePanel");
        frame.setContentPane(new JBeantoTsBeanFolderMapTablePanel().folderMapPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        folderMapPanel = new JPanel();
        folderMapPanel.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        addButton = new JButton();
        addButton.setText("增加映射");
        folderMapPanel.add(addButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        folderMapPanel.add(spacer1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        removeButton = new JButton();
        removeButton.setText("删除映射");
        folderMapPanel.add(removeButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        jScrollPane = new JScrollPane();
        folderMapPanel.add(jScrollPane, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        table1.setDropMode(DropMode.ON_OR_INSERT_ROWS);
        table1.setSurrendersFocusOnKeystroke(false);
        jScrollPane.setViewportView(table1);
        final Spacer spacer2 = new Spacer();
        folderMapPanel.add(spacer2, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return folderMapPanel;
    }

    private void createUIComponents() {

        JBean2TsBeanComponent instance = JBean2TsBeanComponent.getInstance();

        Vector rowDate = new Vector();
        List<String> folderMappingList = instance.getFolderMappingList();
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

        table1 = new JBTable();
        table1.setModel(new DefaultTableModel(rowDate, columnData));
        table1.setBounds(0, 0, 400, 180);
        table1.setVisible(true);
    }


}
