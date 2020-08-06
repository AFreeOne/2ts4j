package org.freeone.util;

import com.intellij.notification.*;

/**
 * idea右下角弹出框的工具类
 */
public class NotificationUtil {

    private static NotificationGroup notificationGroup = new NotificationGroup("notificationGroup", NotificationDisplayType.BALLOON, true);

    public static final String INFORMATION = "INFORMATION";

    public static final String WARNING = "WARNING";

    public static final String ERROR = "ERROR";

    /**
     * 创建一个通知并显示在右下角
     * @param content 通知的内容
     * @param type 通知的类型 ,{@link NotificationUtil#INFORMATION},{@link NotificationUtil#WARNING},{@link NotificationUtil#ERROR}
     */
    public static void createNotification(String content, String type) {
        Notification notification = null;
        if (INFORMATION.equals(type)) {
            notification = notificationGroup.createNotification(content, NotificationType.INFORMATION);
        } else if (WARNING.equals(type)) {
            notification = notificationGroup.createNotification(content, NotificationType.WARNING);
        } else {
            notification = notificationGroup.createNotification(content, NotificationType.ERROR);
        }
        Notifications.Bus.notify(notification);
    }


}
