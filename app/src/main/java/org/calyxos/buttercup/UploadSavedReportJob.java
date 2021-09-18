package org.calyxos.buttercup;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.calyxos.buttercup.model.FeedbackViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UploadSavedReportJob extends Worker {

    private final Context mContext;

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
        //TODO add notification for this.
        fvm.submitCrashReports(mContext, list);
        return Result.success();
    }
}
