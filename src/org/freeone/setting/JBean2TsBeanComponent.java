package org.freeone.setting;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * https://www.jianshu.com/p/e6d49e3c7c60
 */
@State(name = "JavaBeanToTsBeanSetting",storages =  @Storage(value = "Java-Bean-To-Ts-Bean-Setting.xml"))
public class JBean2TsBeanComponent implements PersistentStateComponent<JBean2TsBeanComponent> {
    /**
     * 设置键值对
     */
    private Map<String, String> settingMap;
    /**
     *文件映射路径
     */
    private List<String> folderList;


    public static JBean2TsBeanComponent getInstance() {
        JBean2TsBeanComponent jBean2TsBeanComponentService = ServiceManager.getService(JBean2TsBeanComponent.class);
        System.err.println(jBean2TsBeanComponentService);
        return jBean2TsBeanComponentService;
    }

    public JBean2TsBeanComponent(){
        init();
    }
    private void init() {
        if (settingMap == null){
            settingMap = new HashMap<>();
        }
        if (folderList == null){
            folderList = new ArrayList<>();
        }
    }
    @Nullable
    @Override
    public JBean2TsBeanComponent getState() {
        return this;
    }
    /**
     * 新的组件状态被加载时，调用该方法，如果IDE运行期间，保存数据的文件被从外部修改，则该方法会被再次调用
     * @param state
     */
    @Override
    public void loadState(@NotNull JBean2TsBeanComponent state) {
        Map<String, String> settingMap = state.getSettingMap();
        List<String> folderList = state.getFolderList();
        setSettingMap(settingMap);
        setFolderList(folderList);
    }

    public Map<String, String> getSettingMap() {
        return settingMap;
    }

    public void setSettingMap(Map<String, String> settingMap) {
        this.settingMap = settingMap;
    }

    public List<String> getFolderList() {
        return folderList;
    }

    public void setFolderList(List<String> folderList) {
        this.folderList = folderList;
    }
}
