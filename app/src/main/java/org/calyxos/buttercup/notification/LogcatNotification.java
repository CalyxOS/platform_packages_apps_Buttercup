package org.calyxos.buttercup.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.calyxos.buttercup.Constants;
import org.calyxos.buttercup.MainActivity;
import org.calyxos.buttercup.R;

public class LogcatNotification {

    private static final String TAG = LogcatNotification.class.getSimpleName();
    private final Context context;

    public LogcatNotification(Context context) {
        this.context = context;
    }

    public void showOrUpdateNotification(boolean update, String updateMessage) {
        Log.d(TAG, "Notification creation started");
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.context, Constants.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(context.getString(R.string.logcat_notification_title))
                .setContentText(context.getString(R.string.logcat_notification_desc))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(context.getString(R.string.logcat_notification_desc)))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(getPendingIntent())
                .setProgress(0, 0, true);

        if (update) {
            builder.setContentText(updateMessage)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(updateMessage))
                    .setProgress(0, 0, false);
        }

        Notification notification = builder.build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (update) {
            if (!updateMessage.equals(context.getString(R.string.logcat_sent)))
                notificationManager.cancel(Constants.LOGCAT_NOTIFICATION_ID);
            else
                notificationManager.notify(Constants.LOGCAT_NOTIFICATION_ID, notification);
        } else
            notificationManager.notify(Constants.LOGCAT_NOTIFICATION_ID, notification);

        Log.d(TAG, "Notification finished and showing");
    }

    private void createNotificationChannel() {
        CharSequence name = context.getString(R.string.logcat_notification_channel_name);
        String description = context.getString(R.string.logcat_notification_channel_description);
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel channel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(context, 0, intent, 0);
    }
}
