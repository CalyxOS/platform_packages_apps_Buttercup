package org.calyxos.buttercup.model;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;

import androidx.lifecycle.ViewModel;

import org.calyxos.buttercup.R;
import org.calyxos.buttercup.network.Network;
import org.calyxos.buttercup.network.RequestListener;
import org.calyxos.buttercup.repo.Repository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            } else requestListener.onValidationFailed(context.getString(R.string.subject_body_empty));
        } else requestListener.onInternetError();
    }

    public synchronized void submitLogcat(Context context, RequestListener requestListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //TODO show progress in a Notification
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
                    String fileBase64 = getBase64(logcat);
                    if (!logcat.isEmpty()) { //in case it returns empty for some reason
                        //repo.submitFeedbackWithAttachment("Logcat", logcat, fileName, fileBase64, requestListener);
                    } else requestListener.onValidationFailed(context.getString(R.string.logcat_not_retrieved));
                }
            }
        }).start();
    }

    private String scrubLogcat(String  logcat) {
        //Note: Escape metacharacters of any new regex patterns with Pattern.quote() before adding it below

        String gpsPattern = "[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?),\\s*[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)";
        //gpsPattern = Pattern.quote(gpsPattern);

        //Modified Patterns.PHONE.pattern() so that it matches only numbers with leading '+' as phone numbers.
        // This exempts dates which we need
        String phonePattern = "(\\+[0-9]+[\\- \\.]*)+" // +<digits><sdd>*
                + "(\\([0-9]+\\)[\\- \\.]*)?"   // (<digits>)<sdd>*
                + "([0-9][0-9\\- \\.]+[0-9])"; // <digit><digit|sdd>+<digit>

        String wordPattern = "phone number|phonenumber|phone-number"; //TODO match this words to remove text that follows it just in case
        //String emailPattern = "(" + Patterns.EMAIL_ADDRESS.pattern() + ")";
        //phonePattern = "(" + phonePattern + ")";

        //concatenate multiple patterns together to use at once on the input sequence //TODO improve this approach
        //String regex = String.join("|", emailPattern, phonePattern/*, Patterns.WEB_URL.pattern(), gpsPattern*/);

        Pattern pattern = Pattern.compile(Patterns.EMAIL_ADDRESS.pattern());
        Matcher matcher = pattern.matcher(logcat);

        String out = "";

        while (matcher.find()) {
            out = matcher.replaceAll(generateReplacement(matcher.group().length()));
        }

        //matcher.usePattern(Pattern.compile("\\b(" + phonePattern + ")\\b"));
        pattern = Pattern.compile(phonePattern);
        matcher = pattern.matcher(out);

        while (matcher.find()) {
            out = matcher.replaceAll(generateReplacement(matcher.group().length()));
        }

        return out;
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

    private String getBase64(String logcat) {
        String base64 = Base64.encodeToString(logcat.getBytes(), Base64.DEFAULT);
        Log.d(TAG, "Logcat Base64:" + base64);
        return base64;
    }

}
