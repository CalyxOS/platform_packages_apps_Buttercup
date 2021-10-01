package org.calyxos.buttercup;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.calyxos.buttercup.adapter.FileAdapter;
import org.calyxos.buttercup.databinding.ActivityMainBinding;
import org.calyxos.buttercup.dialog.AlertDialogFragment;
import org.calyxos.buttercup.model.FeedbackViewModel;
import org.calyxos.buttercup.network.RequestListener;
import org.calyxos.buttercup.notification.FeedbackNotification;
import org.calyxos.buttercup.notification.LogcatNotification;
import org.calyxos.buttercup.service.PopupWindowService;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding binding;
    private FeedbackViewModel feedbackViewModel;
    private FileAdapter adapter;
    private AlertDialogFragment dialog;
    private String message = "";
    private boolean resumeDialog = false;

    private ServiceConnection serviceConnection;
    private PopupWindowService popupService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        feedbackViewModel = new FeedbackViewModel();

        adapter = new FileAdapter(this, feedbackViewModel);
        binding.attachmentsList.setAdapter(adapter);

        dialog = new AlertDialogFragment();

        feedbackViewModel.getScreenshots().observe(this, images -> {
            adapter.addFileList(images);
            adapter.notifyDataSetChanged();
        });

        FeedbackNotification feedbackNotification = new FeedbackNotification(this);
        RequestListener requestListener = new RequestListener() {
            @Override
            public void onInternetError() {
                binding.progressBar.setVisibility(View.GONE);
                feedbackNotification.showOrUpdateNotification(true, getString(R.string.internet_unavailable));

                try {
                    dialog.setMessage(getString(R.string.internet_unavailable));
                    dialog.show(getSupportFragmentManager(), "AlertDialog");
                } catch (IllegalStateException e){
                    showDialogOnResume(getString(R.string.internet_unavailable));
                }
            }

            @Override
            public void onValidationFailed(String validationErrorMessage) {
                binding.progressBar.setVisibility(View.GONE);
                feedbackNotification.showOrUpdateNotification(true, validationErrorMessage);

                try {
                    dialog.setMessage(validationErrorMessage);
                    dialog.show(getSupportFragmentManager(), "AlertDialog");
                } catch (IllegalStateException e){
                    showDialogOnResume(validationErrorMessage);
                }
            }

            @Override
            public void onConnectionError(String errorMessage) {
                binding.progressBar.setVisibility(View.GONE);
                feedbackNotification.showOrUpdateNotification(true, errorMessage);

                try {
                    dialog.setMessage(errorMessage);
                    dialog.show(getSupportFragmentManager(), "AlertDialog");
                } catch (IllegalStateException e){
                    showDialogOnResume(errorMessage);
                }
            }

            @Override
            public void onSuccess() {
                binding.progressBar.setVisibility(View.GONE);
                binding.subjectEdit.setText("");
                binding.bodyEdit.setText("");
                feedbackViewModel.clearFileList();
                feedbackNotification.showOrUpdateNotification(true, getString(R.string.feedback_sent));

                try {
                    dialog.setMessage(getString(R.string.feedback_sent));
                    dialog.show(getSupportFragmentManager(), "AlertDialog");
                } catch (IllegalStateException e){
                    showDialogOnResume(getString(R.string.feedback_sent));
                }
            }

            @Override
            public void onFail(String failMessage) {
                binding.progressBar.setVisibility(View.GONE);
                feedbackNotification.showOrUpdateNotification(true, failMessage);

                try {
                    dialog.setMessage(failMessage);
                    dialog.show(getSupportFragmentManager(), "AlertDialog");
                } catch (IllegalStateException e){
                    showDialogOnResume(failMessage);
                }
            }
        };

        binding.submitBtn.setOnClickListener(v -> {
            //TODO hide keyboard
            binding.progressBar.setVisibility(View.VISIBLE);
            feedbackNotification.showOrUpdateNotification(false, null);
            //TODO add tags, links and attachments features later
            feedbackViewModel.submitFeedback(MainActivity.this, binding.subjectEdit.getText().toString(),
                    binding.bodyEdit.getText().toString(), requestListener);
        });

        binding.sendLogcatTxtBtn.setOnClickListener(v -> {
            //TODO enable user pick a date of the logcat (DatePicker)
            binding.progressBar2.setVisibility(View.VISIBLE);
            LogcatNotification logcatNotification = new LogcatNotification(this);
            logcatNotification.showOrUpdateNotification(false, null);

            feedbackViewModel.submitLogcat(MainActivity.this, new RequestListener() {
                @Override
                public void onInternetError() {
                    binding.progressBar2.setVisibility(View.GONE);
                    logcatNotification.showOrUpdateNotification(true, getString(R.string.internet_unavailable));

                    try {
                        dialog.setMessage(getString(R.string.internet_unavailable));
                        dialog.show(getSupportFragmentManager(), "AlertDialog");
                    } catch (IllegalStateException e){
                        showDialogOnResume(getString(R.string.internet_unavailable));
                    }
                }

                @Override
                public void onValidationFailed(String validationErrorMessage) {
                    binding.progressBar2.setVisibility(View.GONE);
                    logcatNotification.showOrUpdateNotification(true, validationErrorMessage);

                    try {
                        dialog.setMessage(validationErrorMessage);
                        dialog.show(getSupportFragmentManager(), "AlertDialog");
                    } catch (IllegalStateException e){
                        showDialogOnResume(validationErrorMessage);
                    }
                }

                @Override
                public void onConnectionError(String errorMessage) {
                    binding.progressBar2.setVisibility(View.GONE);
                    logcatNotification.showOrUpdateNotification(true, errorMessage);

                    try {
                        dialog.setMessage(errorMessage);
                        dialog.show(getSupportFragmentManager(), "AlertDialog");
                    } catch (IllegalStateException e){
                        showDialogOnResume(errorMessage);
                    }
                }

                @Override
                public void onSuccess() {
                    binding.progressBar2.setVisibility(View.GONE);
                    logcatNotification.showOrUpdateNotification(true, getString(R.string.logcat_sent));

                    try {
                        dialog.setMessage(getString(R.string.logcat_sent));
                        dialog.show(getSupportFragmentManager(), "AlertDialog");
                    } catch (IllegalStateException e){
                        showDialogOnResume(getString(R.string.logcat_sent));
                    }
                }

                @Override
                public void onFail(String failMessage) {
                    binding.progressBar2.setVisibility(View.GONE);
                    logcatNotification.showOrUpdateNotification(true, failMessage);

                    try {
                        dialog.setMessage(failMessage);
                        dialog.show(getSupportFragmentManager(), "AlertDialog");
                    } catch (IllegalStateException e){
                        showDialogOnResume(failMessage);
                    }
                }
            });
        });

        binding.takeScreenshotSwitch.setOnClickListener(v -> {
            if (binding.takeScreenshotSwitch.isChecked()) {
                // Is Draw over other apps permission granted?
                if (!Settings.canDrawOverlays(this)) {
                    binding.takeScreenshotSwitch.setChecked(false);
                    //TODO add an explanation dialog here
                    getDrawOverlaysPermission();
                } else {
                    getScreenCapturePermission();
                }
            } else stopPopupService();
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        //Retrieve screenshot image sent
        feedbackViewModel.processScreenshot(this, intent);
    }

    private void startPopupService(int resultCode, Intent data) {
        Log.d(TAG, "Service about to be started");
        Intent serviceIntent = new Intent(MainActivity.this, PopupWindowService.class);
        serviceIntent.putExtra(Constants.RESULT_CODE, resultCode);
        serviceIntent.putExtra(Constants.PERMISSION_DATA, data);
        //Service connection to bind the service to this context because of startForegroundService issues
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "Service connected");
                PopupWindowService.ServiceBinder binder = (PopupWindowService.ServiceBinder) service;
                popupService = binder.getService();
                startForegroundService(serviceIntent);
                popupService.startForeground(Constants.SCREENSHOT_NOTIFICATION_ID, popupService.getNotification());
            }

            @Override
            public void onBindingDied(ComponentName name) {
                Log.w(TAG, "Binding has died.");
            }

            @Override
            public void onNullBinding(ComponentName name) {
                Log.w(TAG, "Binding was null.");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.w(TAG, "Service is disconnected..");
            }
        };

        try {
            Log.d(TAG, "Service bound");
            bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } catch (RuntimeException ignored) {
            Log.d(TAG, "Runtime exception");
            //Use the normal way and accept it will fail sometimes
            startForegroundService(serviceIntent);
        }
    }

    private void stopPopupService() {
        if (serviceConnection != null)
            unbindService(serviceConnection);
        if (popupService != null)
            popupService.stopForeground(true);
        stopService(new Intent(this, PopupWindowService.class));
    }

    private void getDrawOverlaysPermission() {
        // send user to the device settings
        Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        startActivity(myIntent);
    }

    private void getScreenCapturePermission() {
        MediaProjectionManager projectionManager =
                (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(projectionManager.createScreenCaptureIntent(), Constants.SCREEN_CAPTURE_PERMISSION_CODE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (resumeDialog) {
            if (dialog == null)
                dialog = new AlertDialogFragment();

            dialog.setMessage(message);
            dialog.show(getSupportFragmentManager(), "AlertDialog");
        }
    }

    private void showDialogOnResume(String message) {
        resumeDialog = true;
        this.message = message;
    }

    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        startActivityForResult(intent, Constants.PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //feedbackViewModel.processResult(this, requestCode, resultCode, data);
        if (requestCode == Constants.SCREEN_CAPTURE_PERMISSION_CODE) {
            if (resultCode == RESULT_OK) {
                // start a service
                startPopupService(resultCode, data);
            } else {
                binding.takeScreenshotSwitch.setChecked(false);
                Log.d(TAG, "User rejected permission for screen capture.");
                Toast.makeText(this, getString(R.string.screen_capture_rejected), Toast.LENGTH_LONG).show();
            }
        }
    }
}