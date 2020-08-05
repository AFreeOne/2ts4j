import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class ApiDocGeneratorAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here

        NotificationGroup notificationGroup = new NotificationGroup("notificationGroup", NotificationDisplayType.BALLOON, true);
        Notification notification = notificationGroup.createNotification("notification", NotificationType.ERROR);
        Notifications.Bus.notify(notification);

    }
}
