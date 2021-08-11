package org.calyxos.buttercup;

import android.content.Context;
import android.util.Log;
import android.util.Patterns;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

public class ScrubberUtils {

    private static final String TAG = ScrubberUtils.class.getSimpleName();

    public static String scrubLogcat(String logcat) {
        //Note: Escape metacharacters of any new regex patterns manually or with Pattern.quote() before adding it below

        String gpsPattern = "[-+]?([1-8]?\\d(\\.\\d+)+|90(\\.0+)?), [-+]?(180(\\.0+)+|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)+)";

        //Modified Patterns.PHONE.pattern() so that it matches only numbers with leading '+' as phone numbers.
        // This exempts dates which we need
        String phonePattern = "(\\+[0-9]+[\\- \\.]*)+" // +<digits><sdd>*
                + "(\\([0-9]+\\)[\\- \\.]*)?"   // (<digits>)<sdd>*
                + "([0-9][0-9\\- \\.]+[0-9])"; // <digit><digit|sdd>+<digit>

        String phonePattern1 = "(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?" +
                "(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)" +
                "|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*" +
                "(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?"; //not a suitable pattern for this. Needs improvement perhaps

        String emailPattern1 = "[a-zA-Z0-9_]+(?:\\.[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+)*" +
                "(@|%40)(?!([a-zA-Z0-9]*\\.[a-zA-Z0-9]*\\.[a-zA-Z0-9]*\\.))(?:[A-Za-z0-9](?:[a-zA-Z0-9-]*[A-Za-z0-9])?\\.)+" +
                "[a-zA-Z](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?";

        String webURLPattern = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        String webURLPattern1 = "\\b(www)\\.[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"; //matches domains like www.twitter.com

        String ipAddressPattern = "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])";

        String phoneInfoPattern = "(msisdn=|mMsisdn=|iccid=|iccid: |mImsi=)[a-zA-Z0-9]*";

        String userInfoPattern = "(UserInfo\\{\\d:)[a-zA-Z0-9\\s]*";

        String acctInfoPattern = "(Account \\{name=)[a-zA-Z0-9]*";

        String imeiPattern = "(\\d){15}";

        //concatenate multiple patterns together to use at once on the input sequence //TODO improve this approach. Test.

        //String emailPattern = "(" + Patterns.EMAIL_ADDRESS.pattern() + ")";
        //phonePattern = "(" + phonePattern + ")";
        //phonePattern1 = "(" + phonePattern1 + ")"; //Note: before using this pattern in this way, remove start and end symbols
        //emailPattern1 = "(" + emailPattern1 + ")";
        //webURLPattern = "(" + webURLPattern + ")";
        //ipAddressPattern = "(" + ipAddressPattern + ")"; //Note: before using this pattern in this way, remove start and end symbols
        //phoneInfoPattern = "(" + phoneInfoPattern + ")";
        //userInfoPattern = "(" + userInfoPattern + ")";
        //acctInfoPattern = "(" + acctInfoPattern + ")";
        //gpsPattern = "(" + gpsPattern + ")";
        //imeiPattern = "(" + imeiPattern + ")";

        //String regex = String.join("|", emailPattern, phonePattern, webURLPattern, ipAddressPattern, phoneInfoPattern, acctInfoPattern,
        // gpsPattern, imeiPattern);

        Pattern emPattern = Pattern.compile(Patterns.EMAIL_ADDRESS.pattern());
        Pattern emPattern1 = Pattern.compile(emailPattern1);
        Pattern phPattern = Pattern.compile(phonePattern);
        Pattern phPattern1 = Pattern.compile(phonePattern1);
        Pattern webPattern = Pattern.compile(webURLPattern);
        Pattern webPattern1 = Pattern.compile(webURLPattern1);
        Pattern ipPattern = Pattern.compile(ipAddressPattern);
        Pattern piPattern = Pattern.compile(phoneInfoPattern, Pattern.CASE_INSENSITIVE);
        Pattern uiPattern = Pattern.compile(userInfoPattern, Pattern.CASE_INSENSITIVE);
        Pattern aiPattern = Pattern.compile(acctInfoPattern, Pattern.CASE_INSENSITIVE);
        Pattern gPattern = Pattern.compile(gpsPattern);
        Pattern imPattern = Pattern.compile(imeiPattern);


        logcat = emPattern1.matcher(logcat).replaceAll("***EMAIL***");
        logcat = phPattern.matcher(logcat).replaceAll("***PHONE***");
        logcat = webPattern.matcher(logcat).replaceAll("***WEB-URL***");
        logcat = webPattern1.matcher(logcat).replaceAll("***WEB-URL***");
        logcat = ipPattern.matcher(logcat).replaceAll("***IP***");
        logcat = piPattern.matcher(logcat).replaceAll("***PHONE-INFO***");
        logcat = uiPattern.matcher(logcat).replaceAll("***USER-INFO***");
        logcat = aiPattern.matcher(logcat).replaceAll("***ACCT-INFO***");
        logcat = gPattern.matcher(logcat).replaceAll("***GPS-CO-ORDINATES***");
        logcat = imPattern.matcher(logcat).replaceAll("***IMEI-NUMBER***");

        //matcher.usePattern(Pattern.compile("\\b(" + phonePattern + ")\\b"));

        return logcat;
    }

    public static String generateReplacement(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++)
            builder.append("*");

        return builder.toString();
    }

    public static String writeLogcatToFile(Context context, String logcat) {
        String filename = "logcat.txt";
        try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(logcat.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "Logcat File: " + context.getFilesDir() + "/" + filename);
        return filename;
    }
}
