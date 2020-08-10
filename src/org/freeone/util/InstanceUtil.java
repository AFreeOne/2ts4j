package org.freeone.util;

import com.intellij.ui.components.JBPanel;
import org.freeone.swing.ApiDocServerSettingPane;
import org.freeone.swing.JBeantoTsBeanFolderMapTablePanel;

/**
 * 单例工具类
 * @author Lq<sup>2</sup>
 */
public class InstanceUtil {

    private static JBeantoTsBeanFolderMapTablePanel jBeantoTsBeanFolderMapTablePanel;

    private static ApiDocServerSettingPane apiDocServerSettingPane;

     public static synchronized JBeantoTsBeanFolderMapTablePanel getJBeantoTsBeanFolderMapTablePanelInstance(){
         if (jBeantoTsBeanFolderMapTablePanel == null){
             jBeantoTsBeanFolderMapTablePanel = new JBeantoTsBeanFolderMapTablePanel();
         }
         return jBeantoTsBeanFolderMapTablePanel;
     }

     public  static synchronized ApiDocServerSettingPane getApiDocServerSettingPane(){
         if (apiDocServerSettingPane == null){
             apiDocServerSettingPane = new ApiDocServerSettingPane();
         }
         return apiDocServerSettingPane;
     }

}
