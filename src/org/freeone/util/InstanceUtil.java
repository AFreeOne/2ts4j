package org.freeone.util;

import org.freeone.swing.JBeantoTsBeanFolderMapTablePanel;

/**
 * 单例工具类
 * @author Lq<sup>2</sup>
 */
public class InstanceUtil {

    private static JBeantoTsBeanFolderMapTablePanel jBeantoTsBeanFolderMapTablePanel;

     public static synchronized JBeantoTsBeanFolderMapTablePanel getJBeantoTsBeanFolderMapTablePanelInstance(){
         if (jBeantoTsBeanFolderMapTablePanel == null){
             jBeantoTsBeanFolderMapTablePanel = new JBeantoTsBeanFolderMapTablePanel();
         }
         return jBeantoTsBeanFolderMapTablePanel;
     }

}
