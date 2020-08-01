package org.freeone.window;

import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MyToolWindow implements ToolWindowFactory {

    Tree rootTree = new Tree();
    ///构造一个 有滚动条的面板
    JBScrollPane scrollPane=new JBScrollPane();
    private final String rootNodeName = "检查规则";

    //定义tree 的根目录
    DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rootNodeName);

    public MyToolWindow(){

        //构造一个treeModel 对象，进行刷新树操作
        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
        rootTree.setModel(treeModel);
        Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize(); //得到屏幕的尺寸
        JPanel mainPanel=new JPanel();
        //设置主面板的大小
        mainPanel.setPreferredSize(new Dimension((int)screenSize.getWidth()-50,(int)screenSize.getHeight()/3*2));
        //tree 设置大小
        rootTree.setPreferredSize(new Dimension((int)screenSize.getWidth()-50,(int)screenSize.getHeight()/3*2));
        //设置滚动条面板位置
        scrollPane.setPreferredSize(new Dimension((int)screenSize.getWidth()-50,(int)screenSize.getHeight()/3*2-50));
        //将tree添加道滚动条面板上
        scrollPane.setViewportView(rootTree);
        //将滚动条面板设置哼可见
        scrollPane.setVisible(true);
        //设置滚动条的滚动速度
        scrollPane.getVerticalScrollBar().setUnitIncrement(15);
        //解决闪烁问题
        scrollPane.getVerticalScrollBar().setDoubleBuffered(true);

        mainPanel.add(scrollPane);

    }
    // 添加行数规则的node
    public void addLineNode(DefaultMutableTreeNode lineNode){
        rootNode = lineNode;
    }
    public void showToolWin(Project project){

        //构造一个treeModel 对象，进行刷新树操作
        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
        rootTree.setModel(treeModel);
        rootTree.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){
                //BUTTON3是鼠标右键 BUTTON2是鼠标中键 BUTTON1是鼠标左键
                // 双击事件
                if(e.getButton()==e.BUTTON1 && e.getClickCount() == 2){
                    //获取点击的tree节点
                    DefaultMutableTreeNode note=(DefaultMutableTreeNode)rootTree.getLastSelectedPathComponent();
                    if(note!=null){
                        Object[] objects = note.getUserObjectPath();
                        String className = (String)objects[0];
                        //查找名称为mapperName的文件
                        PsiFile[] files = PsiShortNamesCache.getInstance(project).getFilesByName(className);
                        if (files.length == 1) {
                            PsiFile psiFile = (PsiFile) files[0];
                            VirtualFile virtualFile = psiFile.getVirtualFile();
                            // 标题内容
                            String tmpStr = (String) note.getUserObject();
                            //打开文件
                            OpenFileDescriptor openFileDescriptor = new OpenFileDescriptor(project, virtualFile);
                            Editor editor = FileEditorManager.getInstance(project).openTextEditor(openFileDescriptor, true);
                            //获取sql所在的行数，这里用了比较笨的方法。api找了很久没找到有什么方法可以获取行号，希望有大神指点
                            //定位到对应的行
                            String lineNumberStr = tmpStr.substring(tmpStr.indexOf("(line ") + 6,tmpStr.indexOf(")"));
                            Integer lineNumber = Integer.valueOf(lineNumberStr);
                            CaretModel caretModel = editor.getCaretModel();
                            LogicalPosition logicalPosition = caretModel.getLogicalPosition();
                            logicalPosition.leanForward(true);
                            LogicalPosition logical = new LogicalPosition(lineNumber, logicalPosition.column);
                            caretModel.moveToLogicalPosition(logical);
                            SelectionModel selectionModel = editor.getSelectionModel();
                            selectionModel.selectLineAtCaret();
                        }
                    }
                }
            }
        });
        //将tree添加道滚动条面板上
        scrollPane.setViewportView(rootTree);
        JPanel mainPanel=new JPanel();
        mainPanel.add(scrollPane);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(mainPanel,"", false);
        // 获取工具窗口,用于输出结果
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("输出");
        toolWindow.getContentManager().removeAllContents(true);
        toolWindow.getContentManager().addContent(content);
        // 将项目对象，ToolWindow的id传入，获取控件对象
        if (toolWindow != null) {
            // 无论当前状态为关闭/打开，进行强制打开ToolWindow
            toolWindow.show(new Runnable() {
                @Override
                public void run() {
                }
            });
        }
    }


    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

    }

    @Override
    public void init(ToolWindow window) {

    }

    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        return false;
    }

    @Override
    public boolean isDoNotActivateOnStart() {
        return false;
    }
}
