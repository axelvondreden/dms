package com.dude.dms.ui;

import com.dude.dms.backend.brain.OptionKey;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public final class Notify {

    private Notify() {
    }

    public static void info(String message) {
        info(message, false);
    }

    public static void info(String message, boolean persistent) {
        Notification notification = create(message, persistent);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.open();
    }

    public static void error(String message) {
        error(message, false);
    }

    public static void error(String message, boolean persistent) {
        Notification notification = create(message, persistent);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.open();
    }

    private static Notification create(String message, boolean persistent) {
        return new Notification(message, persistent ? 0 : 3000, Notification.Position.valueOf(OptionKey.NOTIFY_POSITION.getString()));
    }
}