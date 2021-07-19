package org.calyxos.buttercup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.calyxos.buttercup.databinding.ActivityMainBinding;
import org.calyxos.buttercup.dialog.AlertDialogFragment;
import org.calyxos.buttercup.model.FeedbackViewModel;
import org.calyxos.buttercup.network.RequestListener;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FeedbackViewModel feedbackViewModel;
    private AlertDialogFragment dialog;

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
    }
}