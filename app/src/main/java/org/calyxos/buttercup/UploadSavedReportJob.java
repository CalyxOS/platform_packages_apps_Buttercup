package org.calyxos.buttercup;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.calyxos.buttercup.model.FeedbackViewModel;
import org.calyxos.buttercup.network.RequestListener;
import org.calyxos.buttercup.notification.CrashReportNotification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UploadSavedReportJob extends Worker {

    private final Context mContext;
    private CrashReportNotification crashReportNotification;

    public UploadSavedReportJob(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Map<String, ?> map = FileUtils.getPreferenceFileContents(mContext);
        FeedbackViewModel fvm = new FeedbackViewModel();
        List<String> list = new ArrayList<>();
        map.forEach((s, o) -> {
            list.add((String)o);
        });

        crashReportNotification = new CrashReportNotification(mContext);
        crashReportNotification.showOrUpdateNotification(false, null);

        fvm.submitCrashReports(mContext, list, getRequestListener());
        return Result.success();
    }

    private RequestListener getRequestListener() {
        return new RequestListener() {

            @Override
            public void onInternetError() {
                crashReportNotification.showOrUpdateNotification(true, mContext.getString(R.string.internet_unavailable));
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
                crashReportNotification.showOrUpdateNotification(true, mContext.getString(R.string.crash_report_sent));
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
