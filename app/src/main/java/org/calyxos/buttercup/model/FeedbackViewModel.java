package org.calyxos.buttercup.model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import org.calyxos.buttercup.FileUtils;
import org.calyxos.buttercup.R;
import org.calyxos.buttercup.ScrubberUtils;
import org.calyxos.buttercup.network.Network;
import org.calyxos.buttercup.network.RequestListener;
import org.calyxos.buttercup.repo.Repository;

public class FeedbackViewModel extends ViewModel {

    private static final String TAG = FeedbackViewModel.class.getSimpleName();
    private final Repository repo;

    public FeedbackViewModel() {
        repo = new Repository();
    }

    public void submitFeedback(Context context, String subject, String body, RequestListener requestListener) {
        if (Network.isConnected(context)) {
            if (!subject.isEmpty() && !body.isEmpty()) {
                repo.submitFeedback(subject, body, requestListener);
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
                        logcat = ScrubberUtils.scrub(logcat);

                        Log.d(TAG, "Scrubbed Logcat {" + logcat + "}");

                        String fileName = ScrubberUtils.writeLogcatToFile(context, logcat);
                        String fileBase64 = FileUtils.getBase64(logcat.getBytes());
                        if (!logcat.isEmpty()) { //in case it returns empty for some reason
                            repo.submitFeedbackWithAttachment("Logcat", "", fileName, fileBase64, requestListener);
                        } else
                            requestListener.onValidationFailed(context.getString(R.string.logcat_not_retrieved));
                    }
                }
            }).start();
        } else requestListener.onInternetError();
    }

    public synchronized void submitCrashReport(Context context, final String report, RequestListener requestListener) {
        if (Network.isConnected(context)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //get crash report
                    Log.d(TAG, "CrashReport {\n" + report + "}");
                    if (!report.isEmpty()) {

                        //scrub crash report of personal information
                        String scrubbedReport = ScrubberUtils.scrub(report);

                        Log.d(TAG, "Scrubbed CrashReport {" + scrubbedReport + "}");

                        String fileName = ScrubberUtils.writeReportToFile(context, scrubbedReport);
                        String fileBase64 = FileUtils.getBase64(scrubbedReport.getBytes());
                        if (!scrubbedReport.isEmpty()) { //in case it returns empty for some reason
                            repo.submitFeedbackWithAttachment("Crash Report", "Crash Report", fileName, fileBase64, requestListener);
                        } else
                            Log.d(TAG, "CrashReport is empty after scrub.");
                    }
                }
            }).start();
        } else {
            Toast.makeText(context, context.getString(R.string.internet_unavailable), Toast.LENGTH_LONG).show();
            //TODO save it somewhere to upload later when network is back up
        }
    }

}
