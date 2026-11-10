package com.siam.tenhour;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

final class TimerNotifications {
    private TimerNotifications() {}
    static void schedule(Context c, long when) {
        if (!AppDatabase.prefs(c).getBoolean("notification", true)) return;
        AlarmManager manager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(c, TimerNotificationReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(c, 7, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        if (Build.VERSION.SDK_INT >= 23) manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, when, pi);
        else manager.set(AlarmManager.RTC_WAKEUP, when, pi);
    }
    static void cancel(Context c) {
        AlarmManager manager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getBroadcast(c, 7, new Intent(c, TimerNotificationReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        manager.cancel(pi);
    }
}
