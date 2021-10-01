package org.calyxos.buttercup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Surface;
import android.view.WindowManager;
import android.view.WindowMetrics;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.nio.ByteBuffer;

public class ScreenshotManager extends Activity implements ImageReader.OnImageAvailableListener {

    private static final String TAG = ScreenshotManager.class.getSimpleName();
    private int mDisplayWidth, mDisplayHeight, mScreenDensity;
    private MediaProjectionManager mProjectionManager;
    private MediaProjection mMediaProjection;
    private ImageReader mImageReader;
    private VirtualDisplay mVirtualDisplay;
    private Surface mSurface;
    private WindowManager mWindowManager;
    private Handler mHandler;

    public final static String ACTION_CAPTURE_SUCCESS = "org.calyxos.buttercup.CAPTURE_SUCCESS";
    public final static String ACTION_CAPTURE_FAILED = "org.calyxos.buttercup.CAPTURE_FAILED";

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // start capture handling thread
        new Thread(() -> {
            Looper.prepare();
            mHandler = new Handler(Looper.myLooper());
            Looper.loop();
        }).start();

        mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        WindowMetrics metrics = mWindowManager.getCurrentWindowMetrics();
        // we could hardcode 720/1280
        mDisplayWidth = metrics.getBounds().width();//Resources.getSystem().getDisplayMetrics().widthPixels;
        mDisplayHeight = metrics.getBounds().height();//Resources.getSystem().getDisplayMetrics().heightPixels;
        mScreenDensity = getResources().getConfiguration().densityDpi; //Resources.getSystem().getDisplayMetrics().densityDpi;

        mImageReader = ImageReader.newInstance(mDisplayWidth,
                mDisplayHeight,
                PixelFormat.RGBA_8888,
                //ImageFormat.FLEX_RGBA_8888,
                2);
        mImageReader.setOnImageAvailableListener(this, mHandler);
        mSurface = mImageReader.getSurface();

        takeShot();
    }

    public void takeShot() {
        if (mSurface == null) {
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_CAPTURE_FAILED));
            finish();
            return;
        }

        Intent intent = getIntent();
        int resultCode = intent.getIntExtra(Constants.RESULT_CODE, RESULT_CANCELED);
        Intent data = intent.getParcelableExtra(Constants.PERMISSION_DATA);

        if (mMediaProjection == null) {
            mMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
            mMediaProjection.registerCallback(new MediaProjectionCallback(), mHandler);
        }
        mVirtualDisplay = createVirtualDisplay();
    }

    private VirtualDisplay createVirtualDisplay() {
        return mMediaProjection.createVirtualDisplay("ButtercupScreenCapture",
                mDisplayWidth, mDisplayHeight, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mSurface, null /*Callbacks*/, mHandler);
    }

    private void stop() {
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
            mVirtualDisplay = null;
        }

        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }

    @Override
    public void onImageAvailable(ImageReader reader) {
        Image image = reader.acquireLatestImage();
        if (image != null) {
            Image.Plane[] planes = image.getPlanes();
            ByteBuffer buffer = planes[0].getBuffer();
            int pixelStride = planes[0].getPixelStride();
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * mDisplayWidth;

            //Create bitmap
            Bitmap bitmap = Bitmap.createBitmap(mDisplayWidth + rowPadding / pixelStride, mDisplayHeight,
                    Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(buffer);

            //crop out black edges
            Rect rect = image.getCropRect();
            bitmap = Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height());

            Intent intent = new Intent(ACTION_CAPTURE_SUCCESS);
            intent.putExtra(Constants.SCREENSHOT_IMAGE, FileUtils.getBytes(bitmap));
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        } else {
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_CAPTURE_FAILED));
        }
        reader.close();
        finish();
    }

    private class MediaProjectionCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            mMediaProjection = null;
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stop();
    }

}
