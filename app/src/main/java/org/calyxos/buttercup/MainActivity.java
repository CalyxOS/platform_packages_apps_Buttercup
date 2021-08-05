package org.calyxos.buttercup;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import org.calyxos.buttercup.databinding.ActivityMainBinding;
import org.calyxos.buttercup.dialog.AlertDialogFragment;
import org.calyxos.buttercup.model.FeedbackViewModel;
import org.calyxos.buttercup.network.RequestListener;

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

        RequestListener requestListener = new RequestListener() {
            @Override
            public void onInternetError() {
                binding.progressBar.setVisibility(View.GONE);

                dialog.setMessage(getString(R.string.internet_unavailable));
                dialog.show(getSupportFragmentManager(), "AlertDialog");
            }

            @Override
            public void onValidationFailed(String validationErrorMessage) {
                binding.progressBar.setVisibility(View.GONE);

                dialog.setMessage(validationErrorMessage);
                dialog.show(getSupportFragmentManager(), "AlertDialog");
            }

            @Override
            public void onConnectionError(String errorMessage) {
                binding.progressBar.setVisibility(View.GONE);

                dialog.setMessage(errorMessage);
                dialog.show(getSupportFragmentManager(), "AlertDialog");
            }

            @Override
            public void onSuccess() {
                binding.progressBar.setVisibility(View.GONE);
                binding.subjectEdit.setText("");
                binding.bodyEdit.setText("");

                dialog.setMessage(getString(R.string.feedback_sent));
                dialog.show(getSupportFragmentManager(), "AlertDialog");
            }

            @Override
            public void onFail(String failMessage) {
                binding.progressBar.setVisibility(View.GONE);

                dialog.setMessage(failMessage);
                dialog.show(getSupportFragmentManager(), "AlertDialog");
            }
        };

        binding.submitBtn.setOnClickListener(v -> {
            //TODO hide keyboard
            binding.progressBar.setVisibility(View.VISIBLE);
            //TODO add tags, links and attachments features later
            feedbackViewModel.submitFeedback(MainActivity.this, binding.subjectEdit.getText().toString(),
                    binding.bodyEdit.getText().toString(), requestListener);
        });

        binding.sendLogcatTxtBtn.setOnClickListener(v -> {
            //TODO enable user pick a date of the logcat (DatePicker)
            binding.progressBar2.setVisibility(View.VISIBLE);

            feedbackViewModel.submitLogcat(MainActivity.this, new RequestListener() {
                @Override
                public void onInternetError() {
                    binding.progressBar2.setVisibility(View.GONE);

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

                    try {
                        dialog.setMessage(failMessage);
                        dialog.show(getSupportFragmentManager(), "AlertDialog");
                    } catch (IllegalStateException e){
                        showDialogOnResume(getString(R.string.logcat_sent));
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