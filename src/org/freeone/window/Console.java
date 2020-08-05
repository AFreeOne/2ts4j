package org.freeone.window;



import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;

/**
 * 自己使用eclipse window builder 工具写的窗口
 */
@Deprecated
public class Console extends JFrame {

	public JPanel contentPane;
	JTextArea textArea;
     int width = 800;
     int height = 500;
     public Console() {
 		setForeground(Color.WHITE);
 		setBackground(Color.BLACK);
 		setFont(new Font("微软雅黑", Font.PLAIN, 20));
 		setTitle("转化结果");
 		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 		setBounds(100, 100, width, height);
 		contentPane = new JPanel();
 		contentPane.setForeground(Color.WHITE);
 		contentPane.setBackground(Color.WHITE);
 		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
 		setContentPane(contentPane);
 		contentPane.setLayout(new CardLayout(0, 0));


 		textArea = new JTextArea();
 		textArea.setFont(new Font("微软雅黑", Font.PLAIN, 16));
 		textArea.setEditable(false);
 		textArea.setRows(8);
 		textArea.setLineWrap(true);
 		textArea.setForeground(Color.WHITE);
 		textArea.setBackground(Color.BLACK);
 		JScrollPane scrollPane = new JScrollPane(textArea);

 		scrollPane.setToolTipText("转换结果滚动面板");
 		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
 		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

 		contentPane.add(scrollPane, "name_6754668365600");
 	}

     public void center(){
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         double screenSizeWidth = screenSize.getWidth();
         double screenSizeHeight = screenSize.getHeight();
         Dimension size = this.getSize();
         double frameWidth = size.getWidth();
         double frameHeight = size.getHeight();
         int x = (int)((screenSizeWidth-frameWidth)/2);
         int y = (int)((screenSizeHeight-frameHeight)/2);
         this.setBounds(x,y,(int)frameWidth,(int)frameHeight);
     }

     public void appendString(String string) {
    	textArea.append(string);
     	textArea.setCaretPosition(textArea.getText().length());
     	contentPane.repaint();
     }
     public void closeInSecond(int second){

		destroy();
	 }

     public void destroy(){
     	this.setDefaultCloseOperation(2);
	 }

    public static void main(String[] args) {
        Console console = new Console();

        console.setVisible(true);
        console.center();

        for (int i = 0; i < 1000; i++) {
        	console.textArea.append("> 在这里添加文本："+i+"\n");
        	console.textArea.setCaretPosition(console.textArea.getText().length());
        	console.contentPane.repaint();
        	try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


    }

}
