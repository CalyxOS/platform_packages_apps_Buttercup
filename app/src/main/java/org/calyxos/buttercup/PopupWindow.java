package org.calyxos.buttercup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static android.content.Context.WINDOW_SERVICE;
import static org.calyxos.buttercup.ScreenshotManager.ACTION_CAPTURE_FAILED;
import static org.calyxos.buttercup.ScreenshotManager.ACTION_CAPTURE_SUCCESS;

public class PopupWindow {

    private final static String TAG = PopupWindow.class.getSimpleName();
    private final Context mContext;
    private final View mView;
    private final WindowManager.LayoutParams mParams;
    private final WindowManager mWindowManager;
    private final RelativeLayout snapBtn;
    private Intent capturePermIntent;
    private int resultCode;

    private BroadcastReceiver mReceiver;

    public PopupWindow(Context context) {
        mContext = context;

        mParams = new WindowManager.LayoutParams(
                // Shrink window to wrap content not fill the screen
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                // Overlay/Draw on top of other application windows
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                // Don't let it grab the input focus
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                // Make the underlying application window visible
                // through any transparent parts
                PixelFormat.TRANSLUCENT);

        LayoutInflater layoutInflater = LayoutInflater.from(context);//(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = layoutInflater.inflate(R.layout.screenshot_popup_window, null);
        snapBtn = mView.findViewById(R.id.popupMain);
        // position of window within screen
        mParams.gravity = Gravity.CENTER | Gravity.END;
        // x and y position values
        //mParams.x = 100;
        //mParams.y = 0;
        //set margins
        //mParams.horizontalMargin = 20.0f;
        //mParams.verticalMargin = 20.0f;

        mWindowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);

        snapBtn.setOnClickListener(view -> {
            //Hide button and take screenshot so it doesn't show in the image
            snapBtn.setVisibility(View.GONE);
            //TODO give user the option of choosing resolution
            Intent intent = new Intent(context, ScreenshotManager.class);
            intent.putExtra(Constants.PERMISSION_DATA, capturePermIntent);
            intent.putExtra(Constants.RESULT_CODE, resultCode);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_CAPTURE_SUCCESS);
        filter.addAction(ACTION_CAPTURE_FAILED);

        mReceiver = getBroadcastReceiver();
        LocalBroadcastManager.getInstance(context).registerReceiver(mReceiver, filter);
    }


    private BroadcastReceiver getBroadcastReceiver() {
        return new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    final String action = intent.getAction();
                    switch (action) {
                        case ACTION_CAPTURE_SUCCESS: {
                            byte[] bitmapBytes = intent.getByteArrayExtra(Constants.SCREENSHOT_IMAGE);
                            //launch/restart main activity for screenshot submission
                            Intent newIntent = new Intent(mContext, MainActivity.class);
                            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            newIntent.putExtra(Constants.SCREENSHOT_IMAGE, bitmapBytes);
                            mContext.startActivity(newIntent);

                            snapBtn.setVisibility(View.VISIBLE);
                            break;
                        }

                        case ACTION_CAPTURE_FAILED: {
                            Log.d(TAG, "Error occurred while capturing screen for some reason.");
                            Toast.makeText(mContext, mContext.getString(R.string.screen_capture_failed), Toast.LENGTH_LONG).show();
                            snapBtn.setVisibility(View.VISIBLE);
                            break;
                        }
                    }
                }
            };
    }

    public void setCapturePermIntent(Intent intent, int code) {
        capturePermIntent = intent;
        resultCode = code;
    }

    public void show() {
        try {
            // check if the view is already
            // inflated or present in the window
            if (mView.getWindowToken() == null) {
                if (mView.getParent() == null) {
                    mWindowManager.addView(mView, mParams);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
            Toast.makeText(mContext, mContext.getString(R.string.popup_window_launch_error), Toast.LENGTH_LONG).show();
        }
    }

    public void close() {
        try {
            // remove the view from the window
            ((WindowManager) mContext.getSystemService(WINDOW_SERVICE)).removeView(mView);
            // invalidate the view
            mView.invalidate();
            // remove all views
            ((ViewGroup) mView.getParent()).removeAllViews();

            // above steps are necessary when adding and removing
            // views simultaneously, it might give some exceptions

            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mReceiver);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
            Toast.makeText(mContext, mContext.getString(R.string.popup_window_close_error), Toast.LENGTH_LONG).show();
        }
    }
}
