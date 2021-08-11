package org.calyxos.buttercup;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.webkit.MimeTypeMap;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;

public class FileUtils {

    public static String getBase64(byte[] data) {
        return Base64.encodeToString(data, Base64.DEFAULT);
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
}
