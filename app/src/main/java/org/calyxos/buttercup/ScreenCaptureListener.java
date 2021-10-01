package org.calyxos.buttercup;

import android.graphics.Bitmap;

public interface ScreenCaptureListener {

    void onCaptureSuccess(Bitmap bitmap);

    void onCaptureFailed();
}
