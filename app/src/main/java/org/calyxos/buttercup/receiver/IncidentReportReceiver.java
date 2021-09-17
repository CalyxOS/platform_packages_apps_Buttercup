package org.calyxos.buttercup.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IncidentManager;

import org.calyxos.buttercup.R;
import org.calyxos.buttercup.model.FeedbackViewModel;
import org.calyxos.buttercup.network.RequestListener;
import org.calyxos.buttercup.notification.CrashReportNotification;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class IncidentReportReceiver extends BroadcastReceiver {

    private IncidentManager mIncidentManager;
    private List<Uri> mList;
    private FeedbackViewModel mFvm;
    private RequestListener mListener;
    private CrashReportNotification crashReportNotification;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        if (intent.getAction().equals("android.intent.action.INCIDENT_REPORT_READY")) {
            mIncidentManager = new IncidentManager(context);
            mList = mIncidentManager.getIncidentReportList(".receiver.IncidentReportReceiver");

            String incidentContent = "";
            for (Uri uri : mList) {
                try (ByteArrayOutputStream os = new ByteArrayOutputStream();
                     InputStream in = mIncidentManager.getIncidentReport(uri).getInputStream()) {

                    byte[] buffer = new byte[4096];
                    for (int len = in.read(buffer); len != -1; len = in.read(buffer)) {
                        os.write(buffer, 0, len);
                    }
                    //Note: we would have used one outputStream object declared outside the loop to write everything
                    //but we want the reports written line by line not mashed up together to make it unreadable.
                    //Try with resource makes this efficient
                    incidentContent = incidentContent.concat("\\n" + new String(os.toByteArray()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mFvm = new FeedbackViewModel();
            mListener = getRequestListener();
            crashReportNotification = new CrashReportNotification(context);
            crashReportNotification.showOrUpdateNotification(false, null);
            mFvm.submitCrashReport(context, incidentContent, mListener);
        }
    }

    private RequestListener getRequestListener() {
        return new RequestListener() {
            @Override
            public void onInternetError() {
                crashReportNotification.showOrUpdateNotification(true, context.getString(R.string.internet_unavailable));
            }

            @Override
            public void onValidationFailed(String validationErrorMessage) {
                crashReportNotification.showOrUpdateNotification(true, validationErrorMessage);
            }

            @Override
            public void onConnectionError(String errorMessage) {
                crashReportNotification.showOrUpdateNotification(true, errorMessage);
            }

            @Override
            public void onSuccess() {
                crashReportNotification.showOrUpdateNotification(true, context.getString(R.string.crash_report_sent));
                //delete incident reports after upload as suggested in documentation??
                //for (Uri uri : mList) mIncidentManager.deleteIncidentReports(uri);
            }

            @Override
            public void onFail(String failMessage) {
                crashReportNotification.showOrUpdateNotification(true, failMessage);
            }
        };
    }
}
