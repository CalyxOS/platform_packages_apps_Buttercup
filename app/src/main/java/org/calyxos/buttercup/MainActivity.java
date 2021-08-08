package org.calyxos.buttercup;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import org.calyxos.buttercup.databinding.ActivityMainBinding;
import org.calyxos.buttercup.dialog.AlertDialogFragment;
import org.calyxos.buttercup.model.FeedbackViewModel;
import org.calyxos.buttercup.network.RequestListener;
import org.calyxos.buttercup.notification.FeedbackNotification;
import org.calyxos.buttercup.notification.LogcatNotification;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FeedbackViewModel feedbackViewModel;
    private AlertDialogFragment dialog;
    private String message = "";
    private boolean resumeDialog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        feedbackViewModel = new FeedbackViewModel();

        dialog = new AlertDialogFragment();

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
}