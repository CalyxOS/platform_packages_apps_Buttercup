package org.calyxos.buttercup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.calyxos.buttercup.databinding.ActivityMainBinding;
import org.calyxos.buttercup.model.FeedbackViewModel;
import org.calyxos.buttercup.network.RequestListener;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FeedbackViewModel feedbackViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        feedbackViewModel = new FeedbackViewModel();

        RequestListener requestListener = new RequestListener() {
            @Override
            public void onInternetError() {
                binding.progressBar.setVisibility(View.GONE);
                //TODO show dialog
            }

            @Override
            public void onValidationFailed(String validationErrorMessage) {
                binding.progressBar.setVisibility(View.GONE);
                //TODO show dialog
            }

            @Override
            public void onConnectionError(String errorMessage) {
                binding.progressBar.setVisibility(View.GONE);
                //TODO show dialog
            }

            @Override
            public void onSuccess() {
                binding.progressBar.setVisibility(View.GONE);
                binding.subjectEdit.setText("");
                binding.bodyEdit.setText("");
                //TODO show dialog
            }

            @Override
            public void onFail(String failMessage) {
                binding.progressBar.setVisibility(View.GONE);
                //TODO show dialog
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