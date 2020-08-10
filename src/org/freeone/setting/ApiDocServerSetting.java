package org.freeone.setting;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import org.freeone.util.InstanceUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

public class ApiDocServerSetting implements Configurable {



    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "文档和服务器";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        JPanel settingPane = InstanceUtil.getApiDocServerSettingPane().settingPane;

        JBean2TsBeanComponent instance = JBean2TsBeanComponent.getInstance();
        Map<String, String> apidocMap = instance.getApidocMap();
        if (apidocMap != null) {
            String serverPath = apidocMap.get("serverPath");
            String jwt = apidocMap.get("jwt");
            serverPath = serverPath == null ? "" : serverPath;
            jwt = jwt == null ? "" : jwt;
            InstanceUtil.getApiDocServerSettingPane().serverPathTextField.setText(serverPath);
            InstanceUtil.getApiDocServerSettingPane().jwtTextField.setText(jwt);
        }
        return settingPane;


    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        JTextField jwtTextField = InstanceUtil.getApiDocServerSettingPane().jwtTextField;
        JTextField serverPathTextField = InstanceUtil.getApiDocServerSettingPane().serverPathTextField;
        JBean2TsBeanComponent setting = JBean2TsBeanComponent.getInstance();
        Map<String, String> apidocMap = setting.getApidocMap();
        apidocMap.put("serverPath",serverPathTextField.getText());
        apidocMap.put("jwt", jwtTextField.getText());

    }
}
