package org.calyxos.buttercup;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.calyxos.buttercup.databinding.ActivityImagePreviewBinding;

public class ImagePreviewActivity extends AppCompatActivity {

    private static final String TAG = ImagePreviewActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityImagePreviewBinding binding = ActivityImagePreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        byte[] bytes = intent.getByteArrayExtra(Constants.SCREENSHOT_IMAGE);

        binding.image.setImageBitmap(FileUtils.getBitmap(bytes));
    }
}
