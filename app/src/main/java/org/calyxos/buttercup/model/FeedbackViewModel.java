package org.calyxos.buttercup.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.calyxos.buttercup.Constants;
import org.calyxos.buttercup.FileUtils;
import org.calyxos.buttercup.MainActivity;
import org.calyxos.buttercup.R;
import org.calyxos.buttercup.ScrubberUtils;
import org.calyxos.buttercup.network.Network;
import org.calyxos.buttercup.network.RequestListener;
import org.calyxos.buttercup.repo.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public void processScreenshot(Context context, Intent intent) {
        if (intent != null) {
            byte[] bytes = intent.getByteArrayExtra(Constants.SCREENSHOT_IMAGE);
            if (bytes != null) {
                if (bytes.length != 0) {
                    //NOTE: if not for security issues with writing an image file to be uploaded to calyx server, we could
                    //write to it here after screenshot is taken. This would be to save it for screen orientation change
                    fileList.add(FileUtils.getImage(context, bytes));
                    mutableLiveData.setValue(fileList);
                }
            }
        }
    }

    public Bundle saveScreenshots(Context context, Bundle outState) {
        List<Image> images = getScreenshots().getValue();
        if (images != null) {
            //This is to avoid the java.lang.RuntimeException: android.os.TransactionTooLargeException: data parcel
            // issues with Android
            //TODO run in diff thread
            images.forEach(image -> {
                if (!isImageSaved(outState, image)) {
                    String url = FileUtils.saveImageToFile(context, image);
                    if (url != null)
                        image.setFileURL(url);
                    image.setDataBytes(null);
                    image.setBase64Data("");
                }
            });
            ArrayList<Parcelable> list = new ArrayList<>(images);
            outState.putParcelableArrayList(MainActivity.SCREENSHOTS, list);
        }
        return outState;
    }

    public void restoreScreenshots(Context context, Bundle saved) {
        if (saved != null) {
            ArrayList<Parcelable> images = saved.getParcelableArrayList(MainActivity.SCREENSHOTS);
            if (images != null) {
                //TODO run in diff thread
                images.forEach(parcelable -> {
                    Image image = (Image) parcelable;
                    byte[] bytes = FileUtils.restoreImageFromFile(context, image);
                    if (bytes != null) {
                        image.setDataBytes(bytes);
                        image.setBase64Data(FileUtils.getBase64(bytes));
                        fileList.add(image);
                    }
                });
                mutableLiveData.setValue(fileList);
            }
        }
    }

    private boolean isImageSaved(Bundle outState, Image image) {
        ArrayList<Parcelable> savedImages = outState.getParcelableArrayList(MainActivity.SCREENSHOTS);
        if (savedImages != null) {
            return savedImages.stream().anyMatch(parcelable -> ((Image) parcelable).getFileName().equals(image.getFileName()));
        } else return false;
    }

    public void processResult(Context context, int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                try {
                    fileList.add(FileUtils.getImage(context, uri));
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
