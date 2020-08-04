package org.freeone.util;

import com.intellij.ui.components.JBPanel;
import org.freeone.swing.JBeantoTsBeanFolderMapTablePanel;

public class InstanceUtil {
    private static JBeantoTsBeanFolderMapTablePanel jBeantoTsBeanFolderMapTablePanel;

     public static synchronized JBeantoTsBeanFolderMapTablePanel getJBeantoTsBeanFolderMapTablePanelInstance(){
         if (jBeantoTsBeanFolderMapTablePanel == null){
             jBeantoTsBeanFolderMapTablePanel = new JBeantoTsBeanFolderMapTablePanel();
         }
         return jBeantoTsBeanFolderMapTablePanel;
     }

}
