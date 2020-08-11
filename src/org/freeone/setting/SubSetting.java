package org.freeone.setting;


import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * https://www.jianshu.com/p/e6d49e3c7c60
 * 子组件SubSetting
 * @author wengyongcheng
 * @since 2020/3/14 10:20 下午
 */
@Deprecated
public class SubSetting implements Configurable {
    private JPanel mainPanel;
    private JTextField testField;
    private JLabel label;

    public SubSetting(){}

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Sub Setting";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return mainPanel;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {

    }
}
