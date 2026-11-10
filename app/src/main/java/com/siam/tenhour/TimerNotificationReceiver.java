package com.siam.tenhour;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class TimerNotificationReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        if (!AppDatabase.active(context) || AppDatabase.paused(context)) return;
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channel = "focus_cycle";
        if (Build.VERSION.SDK_INT >= 26) manager.createNotificationChannel(new NotificationChannel(channel, "পড়ার বিরতি", NotificationManager.IMPORTANCE_DEFAULT));
        PendingIntent open = PendingIntent.getActivity(context, 8, new Intent(context, FocusActivity.class), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Notification.Builder builder = Build.VERSION.SDK_INT >= 26 ? new Notification.Builder(context, channel) : new Notification.Builder(context);
        Notification n = builder.setSmallIcon(R.drawable.ic_clock).setContentTitle("এক ঘণ্টা পূর্ণ হয়েছে")
                .setContentText("দুই মিনিট বিশ্রাম নিন।").setContentIntent(open).setAutoCancel(true).build();
        try { manager.notify(7, n); } catch (SecurityException ignored) { }
        if (AppDatabase.prefs(context).getBoolean("sound", true)) {
            try { android.media.ToneGenerator tone = new android.media.ToneGenerator(android.media.AudioManager.STREAM_NOTIFICATION, 65); tone.startTone(android.media.ToneGenerator.TONE_PROP_ACK, 450); } catch (Exception ignored) { }
        }
        if (AppDatabase.prefs(context).getBoolean("vibration", true)) {
            android.os.Vibrator vibrator = (android.os.Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= 26) vibrator.vibrate(android.os.VibrationEffect.createOneShot(350, android.os.VibrationEffect.DEFAULT_AMPLITUDE));
                else vibrator.vibrate(350);
            }
        }
    }
}
