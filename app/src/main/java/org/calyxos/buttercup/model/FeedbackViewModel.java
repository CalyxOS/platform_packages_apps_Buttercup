package org.calyxos.buttercup.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.calyxos.buttercup.Constants;
import org.calyxos.buttercup.FileUtils;
import org.calyxos.buttercup.R;
import org.calyxos.buttercup.ScrubberUtils;
import org.calyxos.buttercup.network.Network;
import org.calyxos.buttercup.network.RequestListener;
import org.calyxos.buttercup.repo.Repository;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FeedbackViewModel extends ViewModel {

    private static final String TAG = FeedbackViewModel.class.getSimpleName();
    private final Repository repo;
    private MutableLiveData<List<Image>> mutableLiveData;
    private final List<Image> fileList = new ArrayList<>();

    public FeedbackViewModel() {
        repo = new Repository();
    }

    public void submitFeedback(Context context, String subject, String body, RequestListener requestListener) {
        if (Network.isConnected(context)) {
            if (!subject.isEmpty() && !body.isEmpty()) {
                repo.submitFeedback(subject, body, fileList, requestListener);
            } else
                requestListener.onValidationFailed(context.getString(R.string.subject_body_empty));
        } else requestListener.onInternetError();
    }

    public synchronized void submitLogcat(Context context, RequestListener requestListener) {
        if (Network.isConnected(context)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //get logcat
                    String logcat = repo.getLogcat();
                    Log.d(TAG, "Logcat {\n" + logcat + "}");
                    if (logcat.isEmpty())
                        requestListener.onValidationFailed(context.getString(R.string.logcat_not_retrieved));
                    else {
                        //scrub logcat of personal information
                        logcat = ScrubberUtils.scrubLogcat(logcat);
                        Log.d(TAG, "Scrubbed Logcat {" + logcat + "}");
                        String fileName = ScrubberUtils.writeLogcatToFile(context, logcat);
                        String fileBase64 = FileUtils.getBase64(logcat.getBytes());
                        if (!logcat.isEmpty()) { //in case it returns empty for some reason
                            repo.submitFeedbackWithAttachment("Logcat", "Logcat", fileName, fileBase64, requestListener);
                        } else
                            requestListener.onValidationFailed(context.getString(R.string.logcat_not_retrieved));
                    }
                }
            }).start();
        } else requestListener.onInternetError();
    }

    public LiveData<List<Image>> getScreenshots() {
        if(mutableLiveData == null){
            mutableLiveData = new MutableLiveData<>();
        }
        return mutableLiveData;
    }

    public void removeFromFileList(Image image) {
        fileList.remove(image);
        mutableLiveData.setValue(fileList);
    }

    public void clearFileList() {
        fileList.clear();
        mutableLiveData.setValue(fileList);
    }

    public void processResult(Context context, int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                try {
                    fileList.add(FileUtils.getMetadata(context, uri));
                    mutableLiveData.setValue(fileList);
                } catch (IOException e) {
                    Toast.makeText(context, context.getString(R.string.image_pick_failed), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, context.getString(R.string.image_pick_failed), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Returned Intent is null for some reason.");
            }
        }
    }

}
