package com.dude.dms.ui.views;

import com.dude.dms.ui.utils.Const;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;

/**
 * Interface for views showing notifications to users
 */
@FunctionalInterface
public interface HasNotifications extends HasElement {

    default void showNotification(String message) {
        showNotification(message, false);
    }

    default void showNotification(String message, boolean error) {
        showNotification(message, error, false);
    }

    default void showNotification(String message, boolean error, boolean persistent) {
        if (persistent) {
            Button close = new Button("Close");
            close.getElement().setAttribute("theme", "tertiary small error");
            Notification notification = new Notification(new Text(message), close);
            notification.setPosition(Position.BOTTOM_START);
            notification.setDuration(0);
            if (error) {
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
            close.addClickListener(event -> notification.close());
            notification.open();
        } else {
            Notification notification = new Notification(message, Const.NOTIFICATION_DURATION, Position.BOTTOM_STRETCH);
            if (error) {
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
            notification.open();
        }
    }
}
