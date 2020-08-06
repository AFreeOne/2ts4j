package org.freeone.util;

import com.intellij.notification.*;

/**
 * idea右下角弹出框的工具类
 */
public class NotificationUtil {

    private static NotificationGroup notificationGroupBalloon = new NotificationGroup("notificationGroup", NotificationDisplayType.BALLOON, true);

    private static NotificationGroup notificationGroupStickyBalloon = new NotificationGroup("notificationGroup", NotificationDisplayType.STICKY_BALLOON, true);

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
            notification = notificationGroupBalloon.createNotification(content, NotificationType.INFORMATION);
        } else if (WARNING.equals(type)) {
            notification = notificationGroupBalloon.createNotification(content, NotificationType.WARNING);
        } else {
            notification = notificationGroupBalloon.createNotification(content, NotificationType.ERROR);
        }
        Notifications.Bus.notify(notification);
    }

    public static void createStickyNotification (String content, String type) {
        Notification notification = null;
        if (INFORMATION.equals(type)) {
            notification = notificationGroupStickyBalloon.createNotification(content, NotificationType.INFORMATION);
        } else if (WARNING.equals(type)) {
            notification = notificationGroupStickyBalloon.createNotification(content, NotificationType.WARNING);
        } else {
            notification = notificationGroupStickyBalloon.createNotification(content, NotificationType.ERROR);
        }
        Notifications.Bus.notify(notification);
    }




}
