package org.calyxos.buttercup.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.calyxos.buttercup.Constants;
import org.calyxos.buttercup.FileUtils;
import org.calyxos.buttercup.ImagePreviewActivity;
import org.calyxos.buttercup.databinding.AttachmentListItemBinding;
import org.calyxos.buttercup.model.FeedbackViewModel;
import org.calyxos.buttercup.model.Image;

import java.util.ArrayList;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {

    private final Context context;
    private final FeedbackViewModel feedbackViewModel;
    private List<Image> fileList = new ArrayList<>();

    public FileAdapter(Context context,  FeedbackViewModel feedbackViewModel) {
        this.context = context;
        this.feedbackViewModel = feedbackViewModel;
    }

    public void addFileList(List<Image> list) {
        fileList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attachment_list_item, parent, false);
        AttachmentListItemBinding binding = AttachmentListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Image image = fileList.get(position);
        holder.binding.fileName.setText(image.getFileName());
        holder.binding.image.setImageBitmap(FileUtils.getBitmap(image.getDataBytes()));

        holder.binding.removeAttachment.setOnClickListener( v ->
                feedbackViewModel.removeFromFileList(fileList.get(holder.getAdapterPosition())));

        holder.binding.image.setOnClickListener(v -> {
            if (image.getDataBytes() != null) {
                Intent intent = new Intent(context, ImagePreviewActivity.class);
                intent.putExtra(Constants.SCREENSHOT_IMAGE_NAME, image.getFileName());
                context.startActivity(intent);
            } else Log.e("FileAdapter", "Image is null for some reason");
        });
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        AttachmentListItemBinding binding;

        public ViewHolder(AttachmentListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
