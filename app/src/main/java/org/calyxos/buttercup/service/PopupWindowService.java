package org.calyxos.buttercup.service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.calyxos.buttercup.Constants;
import org.calyxos.buttercup.FileUtils;
import org.calyxos.buttercup.MainActivity;
import org.calyxos.buttercup.PopupWindow;
import org.calyxos.buttercup.R;
import org.calyxos.buttercup.model.FeedbackViewModel;
import org.calyxos.buttercup.model.Image;

import java.util.ArrayList;
import java.util.List;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;
import static org.calyxos.buttercup.ScreenshotManager.ACTION_CAPTURE_FAILED;
import static org.calyxos.buttercup.ScreenshotManager.ACTION_CAPTURE_SUCCESS;

public class PopupWindowService extends Service {

    private static final String TAG = PopupWindowService.class.getSimpleName();
    private static PopupWindowService instance;
    private PopupWindow mPopupWindow;
    private FeedbackViewModel feedbackViewModel;

    public class ServiceBinder extends Binder {
        public PopupWindowService getService() {
            return PopupWindowService.this;
        }
    }

    private final ServiceBinder binder = new ServiceBinder();

    @Override
    public void onCreate() {
        instance = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            mPopupWindow = new PopupWindow(this);
            mPopupWindow.setCapturePermIntent(intent.getParcelableExtra(Constants.PERMISSION_DATA),
                    intent.getIntExtra(Constants.RESULT_CODE, Activity.RESULT_CANCELED));
            mPopupWindow.show();

            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_CAPTURE_SUCCESS);
            filter.addAction(ACTION_CAPTURE_FAILED);

            BroadcastReceiver mReceiver = getBroadcastReceiver();
            LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);
        } else return START_NOT_STICKY;
        return START_STICKY;
    }

    private BroadcastReceiver getBroadcastReceiver() {
        return new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                switch (action) {
                    case ACTION_CAPTURE_SUCCESS: {
                        byte[] bitmapBytes = intent.getByteArrayExtra(Constants.SCREENSHOT_IMAGE);

                        feedbackViewModel.addNewScreenshot(FileUtils.getImage(context, bitmapBytes));
                        showCapturedNotification();
                        break;
                    }

                    case ACTION_CAPTURE_FAILED: {
                        Log.d(TAG, "Error occurred while capturing screen for some reason.");
                        Toast.makeText(context, getString(R.string.screen_capture_failed), Toast.LENGTH_LONG).show();
                        break;
                    }
                }
            }
        };
    }

    public void setViewModel(FeedbackViewModel viewModel) {
        feedbackViewModel = viewModel;
    }

    public static PopupWindowService getInstance() {
        return instance;
    }

    public Notification getNotification() {
        Log.d(TAG, "Notification creation started");
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID_2)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(getString(R.string.screenshot_notification_title))
                .setContentText(getString(R.string.default_config_notification_desc))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(getString(R.string.default_config_notification_desc)))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(getPendingIntent())
                .addAction(R.drawable.ic_baseline_close_24, getString(R.string.disable), getStopActionPendingIntent());

        Notification notification = builder.build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(Constants.SCREENSHOT_NOTIFICATION_ID, notification);



        Log.d(TAG, "Notification finished and showing");

        return notification;
    }

    private void createNotificationChannel() {
        CharSequence name = getString(R.string.screenshot_notification_channel_name);
        String description = getString(R.string.screenshot_notification_channel_description);
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel channel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID_2, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(this, 0, intent, 0);
    }

    private PendingIntent getStopActionPendingIntent() {
        Log.d(TAG, "Stop pending intent creation started");
        Intent stopIntent = new Intent(this, StopActionReceiver.class);
        stopIntent.setAction(Constants.ACTION_STOP);
        stopIntent.putExtra(EXTRA_NOTIFICATION_ID, Constants.SCREENSHOT_NOTIFICATION_ID);
        stopIntent.setClass(this, StopActionReceiver.class);
        return PendingIntent.getBroadcast(this, 12, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void showCapturedNotification() {
        createCapturedNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID_3)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(getString(R.string.new_screenshot_taken))
                .setContentText(getString(R.string.capture_information))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(getString(R.string.capture_information)))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(getPendingIntent())
                .setAutoCancel(true);

        Notification notification = builder.build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(Constants.SCREENSHOT_NOTIFICATION_ID_1, notification);

    }

    private void createCapturedNotificationChannel() {
        CharSequence name = getString(R.string.screenshot_captured_notification_channel_name);
        String description = getString(R.string.screenshot_captured_notification_channel_description);
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel channel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID_3, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    public void stopService() {
        Log.d(TAG, "Stop service called");
        mPopupWindow.close();
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
        mPopupWindow.close();
        feedbackViewModel = null;
        stopForeground(true);
    }

    public static class StopActionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Stop action receiver started");
            String action = intent.getAction();
            Log.d(TAG, "Action: " + action);
            if (action.equals(Constants.ACTION_STOP)) {
                int notificationId = intent.getExtras().getInt(EXTRA_NOTIFICATION_ID);
                Log.d(TAG, "Extra Noti Id: " + notificationId);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.cancel(null, notificationId);

                PopupWindowService popupService = PopupWindowService.getInstance();
                popupService.stopForeground(true);

                popupService.mPopupWindow.close();
                popupService.stopService();

                Log.d(TAG, "Stop action receiver ended");
            }
        }
    }
}
