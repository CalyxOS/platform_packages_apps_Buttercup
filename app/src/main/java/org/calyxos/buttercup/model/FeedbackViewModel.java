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
import org.calyxos.buttercup.R;
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
                        logcat = scrubLogcat(logcat);
                        Log.d(TAG, "Scrubbed Logcat {" + logcat + "}");
                        String fileName = writeLogcatToFile(context, logcat);
                        //String fileBase64 = getBase64(logcat);
                        if (!logcat.isEmpty()) { //in case it returns empty for some reason
                            //repo.submitFeedbackWithAttachment("Logcat", "", fileName, fileBase64, requestListener);
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

    private String scrubLogcat(String logcat) {
        //Note: Escape metacharacters of any new regex patterns manually or with Pattern.quote() before adding it below

        String gpsPattern = "[-+]?([1-8]?\\d(\\.\\d+)+|90(\\.0+)?), [-+]?(180(\\.0+)+|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)+)";

        //Modified Patterns.PHONE.pattern() so that it matches only numbers with leading '+' as phone numbers.
        // This exempts dates which we need
        String phonePattern = "(\\+[0-9]+[\\- \\.]*)+" // +<digits><sdd>*
                + "(\\([0-9]+\\)[\\- \\.]*)?"   // (<digits>)<sdd>*
                + "([0-9][0-9\\- \\.]+[0-9])"; // <digit><digit|sdd>+<digit>

        String phonePattern1 = "(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?" +
                "(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)" +
                "|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*" +
                "(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?"; //not a suitable pattern for this. Needs improvement perhaps

        String emailPattern1 = "[a-zA-Z0-9_]+(?:\\.[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+)*" +
                "(@|%40)(?!([a-zA-Z0-9]*\\.[a-zA-Z0-9]*\\.[a-zA-Z0-9]*\\.))(?:[A-Za-z0-9](?:[a-zA-Z0-9-]*[A-Za-z0-9])?\\.)+" +
                "[a-zA-Z](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?";

        String webURLPattern = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        String webURLPattern1 = "\\b(www)\\.[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"; //matches domains like www.twitter.com

        String ipAddressPattern = "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])";

        String phoneInfoPattern = "(msisdn=|mMsisdn=|iccid=|iccid: |mImsi=)[a-zA-Z0-9]*";

        String userInfoPattern = "(UserInfo\\{\\d:)[a-zA-Z0-9\\s]*";

        String acctInfoPattern = "(Account \\{name=)[a-zA-Z0-9]*";

        String imeiPattern = "(\\d){15}";

        //concatenate multiple patterns together to use at once on the input sequence //TODO improve this approach. Test.

        //String emailPattern = "(" + Patterns.EMAIL_ADDRESS.pattern() + ")";
        //phonePattern = "(" + phonePattern + ")";
        //phonePattern1 = "(" + phonePattern1 + ")"; //Note: before using this pattern in this way, remove start and end symbols
        //emailPattern1 = "(" + emailPattern1 + ")";
        //webURLPattern = "(" + webURLPattern + ")";
        //ipAddressPattern = "(" + ipAddressPattern + ")"; //Note: before using this pattern in this way, remove start and end symbols
        //phoneInfoPattern = "(" + phoneInfoPattern + ")";
        //userInfoPattern = "(" + userInfoPattern + ")";
        //acctInfoPattern = "(" + acctInfoPattern + ")";
        //gpsPattern = "(" + gpsPattern + ")";
        //imeiPattern = "(" + imeiPattern + ")";

        //String regex = String.join("|", emailPattern, phonePattern, webURLPattern, ipAddressPattern, phoneInfoPattern, acctInfoPattern,
        // gpsPattern, imeiPattern);

        Pattern emPattern = Pattern.compile(Patterns.EMAIL_ADDRESS.pattern());
        Pattern emPattern1 = Pattern.compile(emailPattern1);
        Pattern phPattern = Pattern.compile(phonePattern);
        Pattern phPattern1 = Pattern.compile(phonePattern1);
        Pattern webPattern = Pattern.compile(webURLPattern);
        Pattern webPattern1 = Pattern.compile(webURLPattern1);
        Pattern ipPattern = Pattern.compile(ipAddressPattern);
        Pattern piPattern = Pattern.compile(phoneInfoPattern, Pattern.CASE_INSENSITIVE);
        Pattern uiPattern = Pattern.compile(userInfoPattern, Pattern.CASE_INSENSITIVE);
        Pattern aiPattern = Pattern.compile(acctInfoPattern, Pattern.CASE_INSENSITIVE);
        Pattern gPattern = Pattern.compile(gpsPattern);
        Pattern imPattern = Pattern.compile(imeiPattern);


        logcat = emPattern1.matcher(logcat).replaceAll("***EMAIL***");
        logcat = phPattern.matcher(logcat).replaceAll("***PHONE***");
        logcat = webPattern.matcher(logcat).replaceAll("***WEB-URL***");
        logcat = webPattern1.matcher(logcat).replaceAll("***WEB-URL***");
        logcat = ipPattern.matcher(logcat).replaceAll("***IP***");
        logcat = piPattern.matcher(logcat).replaceAll("***PHONE-INFO***");
        logcat = uiPattern.matcher(logcat).replaceAll("***USER-INFO***");
        logcat = aiPattern.matcher(logcat).replaceAll("***ACCT-INFO***");
        logcat = gPattern.matcher(logcat).replaceAll("***GPS-CO-ORDINATES***");
        logcat = imPattern.matcher(logcat).replaceAll("***IMEI-NUMBER***");

        //matcher.usePattern(Pattern.compile("\\b(" + phonePattern + ")\\b"));

        return logcat;
    }

    private String generateReplacement(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++)
            builder.append("*");

        return builder.toString();
    }

    private String writeLogcatToFile(Context context, String logcat) {
        String filename = "logcat.txt";
        try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(logcat.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "Logcat File: " + context.getFilesDir() + "/" + filename);
        return filename;
    }

    private String getBase64(byte[] data) {
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    private Image getMetadata(Context context, Uri uri) throws IOException {
        String name = "Screenshot";
        String extension = "png";
        String mimeType = "image/*";
        int fileSize = 0;
        Image image = new Image();

        //check provider
        if (uri.getScheme().equals("content")) {
            String displayName = getContentSchemeDisplayName(context, uri);
            name = displayName == null? name : displayName;

        } else if (uri.getScheme().equals("file")) {
            String displayName = getFileSchemeDisplayName(uri);
            name = displayName == null? name : displayName;
        }

        extension = getFileExtension(context, uri);
        mimeType = getMimeType(context, uri);
        fileSize = getFileSize(context, uri);

        byte[] dataBytes = getBytes(context, uri, extension);
        image.setData(getBase64(dataBytes));
        image.setFileName(name);
        image.setMimeType(mimeType);
        image.setFileSize(fileSize == 0? dataBytes.length : fileSize);

        return image;
    }

    private String getContentSchemeDisplayName(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null,
                null);

        try {
            if (cursor != null && cursor.moveToFirst()) {

                //try and get file name
                String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                /*if (displayName == null || displayName.isEmpty()) {
                    String[] proj = { MediaStore.Images.Media.TITLE };
                    Cursor cursor1 = context.getContentResolver().query(uri, proj, null, null, null);
                    if (cursor1 != null && cursor1.getCount() != 0) {
                        cursor1.moveToFirst();
                        displayName = cursor1.getString(cursor1.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE));
                        if (displayName != null && !displayName.isEmpty())
                            return displayName;
                    }
                    if (cursor1 != null) {
                        cursor1.close();
                    }
                } else*/ return displayName;
            }
        } finally {
            assert cursor != null;
            cursor.close();
        }

        return null;
    }

    private String getFileSchemeDisplayName(Uri uri) {
        return uri.getLastPathSegment();
    }

    private int getFileSize(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null,
                null);
        try {
            //try and get file size
            if (cursor != null && cursor.moveToFirst()) {
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                String size = null;
                if (!cursor.isNull(sizeIndex)) {
                    size = cursor.getString(sizeIndex);
                    return Integer.parseInt(size);
                } /*else {
                //try this
                String[] proj = {MediaStore.Images.Media.SIZE};
                Cursor cursor2 = context.getContentResolver().query(uri, proj, null, null, null);
                if (cursor2 != null && cursor2.getCount() != 0) {
                    cursor2.moveToFirst();
                    size = cursor2.getString(cursor2.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));
                    if (size != null && !size.isEmpty())
                        return Integer.parseInt(size);
                }
                if (cursor2 != null) {
                    cursor2.close();
                }
            }*/
            }
        } finally {
            assert cursor != null;
            cursor.close();
        }
        return 0;
    }

    private String getMimeType(Context context, Uri uri) {
        return context.getContentResolver().getType(uri);
    }

    private String getFileExtension(Context context, Uri uri) {
        //try and get the extension
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(context.getContentResolver().getType(uri));
    }

    private byte[] getBytes(Context context, Uri uri, String extension) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(extension.equalsIgnoreCase("jpg") ? Bitmap.CompressFormat.JPEG : Bitmap.CompressFormat.PNG,
                100, stream);

        image.recycle();
        return stream.toByteArray();
    }

    public void processResult(Context context, int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                try {
                    fileList.add(getMetadata(context, uri));
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
