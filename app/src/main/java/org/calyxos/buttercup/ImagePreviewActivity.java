package org.calyxos.buttercup;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.calyxos.buttercup.databinding.ActivityImagePreviewBinding;
import org.calyxos.buttercup.model.FeedbackViewModel;
import org.calyxos.buttercup.model.Image;

import java.util.List;

public class ImagePreviewActivity extends AppCompatActivity {

    private static final String TAG = ImagePreviewActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityImagePreviewBinding binding = ActivityImagePreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String name = intent.getStringExtra(Constants.SCREENSHOT_IMAGE_NAME);
        List<Image> imageList = FeedbackViewModel.getFeedbackViewModel().getScreenshots().getValue();
        if (imageList != null)
            imageList.forEach(image -> {
                if (image.getFileName().equals(name))
                    binding.image.setImageBitmap(FileUtils.getBitmap(image.getDataBytes()));
            });
    }
}
