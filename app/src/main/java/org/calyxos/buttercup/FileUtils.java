package org.calyxos.buttercup;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.webkit.MimeTypeMap;

import org.calyxos.buttercup.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();

    public static String getBase64(byte[] data) {
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    public static Image getImage(Context context, Uri uri) throws IOException {
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
        image.setBase64Data(getBase64(dataBytes));
        image.setDataBytes(dataBytes);
        image.setFileName(name);
        image.setMimeType(mimeType);
        image.setFileSize(fileSize == 0? dataBytes.length : fileSize);

        return image;
    }

    public static Image getImage(Context context, byte[] bytes) {
        String name = "Screenshot";
        String extension = "jpg";
        String mimeType = "image/*";
        int fileSize = 0;
        Image image = new Image();

        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy_KK:mm:ss:SSS", Locale.getDefault());
        name = df.format(c);
        name = name + "." + extension;

        fileSize = bytes.length;

        image.setBase64Data(getBase64(bytes));
        image.setDataBytes(bytes);
        image.setFileName(name);
        image.setMimeType(mimeType);
        image.setFileSize(fileSize);

        return image;
    }

    public static String getContentSchemeDisplayName(Context context, Uri uri) {
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

    public static String getFileSchemeDisplayName(Uri uri) {
        return uri.getLastPathSegment();
    }

    public static int getFileSize(Context context, Uri uri) {
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

    public static String getMimeType(Context context, Uri uri) {
        return context.getContentResolver().getType(uri);
    }

    private static String getFileExtension(Context context, Uri uri) {
        //try and get the extension
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(context.getContentResolver().getType(uri));
    }

    public static byte[] getBytes(Context context, Uri uri, String extension) throws IOException {
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

    public static byte[] getBytes(Bitmap image) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        image.recycle();
        return stream.toByteArray();
    }

    public static Bitmap getBitmap(byte[] bytes) {
        if (bytes != null)
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        else return null;
    }

    public static String saveImageToFile(Context context, Image image) {
        try (FileOutputStream fos = context.openFileOutput(image.getFileName(), Context.MODE_PRIVATE)) {
            if (!new File(context.getFilesDir() + "/" + image.getFileName()).exists()) {
                fos.write(image.getDataBytes());
                fos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Log.e(TAG, "Image File: " + context.getFilesDir() + "/" + image.getFileName());
        return context.getFilesDir() + "/" + image.getFileName();
    }

    public static byte[] restoreImageFromFile(Context context, Image image) {
        byte[] bytes = new byte[image.getFileSize()];
        try (FileInputStream fis = context.openFileInput(context.getFilesDir() + "/" + image.getFileName())) {
            int i = fis.read(bytes);
            fis.close();
            //delete file
            boolean b = new File(context.getFilesDir() + "/" + image.getFileName()).delete();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return bytes;
    }
}
