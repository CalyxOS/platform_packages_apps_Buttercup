package org.calyxos.buttercup;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.Date;
import java.util.Locale;

public class Utils {

    public static String getCurrentDateTime() {
        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy KK:mm:ss:SSS", Locale.getDefault());
        return df.format(c);
    }

    public static void setWorkRequest(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest job = new OneTimeWorkRequest.Builder(UploadSavedReportJob.class)
                .setConstraints(constraints).build();
        WorkManager.getInstance(context).enqueue(job);
    }
}
