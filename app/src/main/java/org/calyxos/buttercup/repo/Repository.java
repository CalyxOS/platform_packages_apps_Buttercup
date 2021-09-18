package org.calyxos.buttercup.repo;

import android.content.Context;
import android.util.Log;

import org.calyxos.buttercup.Constants;
import org.calyxos.buttercup.FileUtils;
import org.calyxos.buttercup.ScrubberUtils;
import org.calyxos.buttercup.model.Ticket;
import org.calyxos.buttercup.model.compat.ArticleAttachmentCompat;
import org.calyxos.buttercup.model.compat.TicketArticleCompat;
import org.calyxos.buttercup.model.compat.TicketCompat;
import org.calyxos.buttercup.network.RequestListener;
import org.calyxos.buttercup.network.RetrofitFactory;
import org.calyxos.buttercup.network.WebServices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Repository {

    private final String TAG = getClass().getSimpleName();
    private final WebServices webServices;

    public Repository() {
        Retrofit retrofit = RetrofitFactory.getRetrofit();
        webServices = retrofit.create(WebServices.class);
    }

    public void submitFeedback(String subject, String body, RequestListener listener) {
        TicketCompat ticketCompat = new TicketCompat();
        ticketCompat.setCustomer(Constants.ZAMMAD_CUSTOMER);
        ticketCompat.setGroup("Users");
        ticketCompat.setTitle(subject);
        ticketCompat.setArticle(new TicketArticleCompat(subject, body, Constants.ARTICLE_TYPE, false, null));
        webServices.createTicket(getHeaderMap(), ticketCompat).enqueue(new Callback<Ticket>() {
            @Override
            public void onResponse(Call<Ticket> call, Response<Ticket> response) {
                if (listener != null) {
                    if (response.isSuccessful()) {
                        listener.onSuccess();
                        assert response.body() != null;
                        Log.d(TAG, response.body().toString());
                    } else {
                        listener.onFail(response.message());
                        assert response.errorBody() != null;
                        try {
                            Log.e(TAG, "Error Body: " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.e(TAG, "Message: " + response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<Ticket> call, Throwable t) {
                if (listener != null)
                    listener.onConnectionError(t.getMessage());

                Log.e(TAG, "Connection Error: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    public void submitFeedbackWithAttachment(String subject, String body, String fileName, String fileBase64, RequestListener listener) {
        TicketCompat ticketCompat = new TicketCompat();
        ticketCompat.setCustomer(Constants.ZAMMAD_CUSTOMER);
        ticketCompat.setGroup("Users");
        ticketCompat.setTitle(subject);
        List<ArticleAttachmentCompat> attachs = new ArrayList<>();
        attachs.add(new ArticleAttachmentCompat(fileName, fileBase64, Constants.MIME_TYPE_TEXT));
        ticketCompat.setArticle(new TicketArticleCompat(subject, body, Constants.ARTICLE_TYPE, false, attachs));
        webServices.createTicket(getHeaderMap(), ticketCompat).enqueue(new Callback<Ticket>() {
            @Override
            public void onResponse(Call<Ticket> call, Response<Ticket> response) {
                if (listener != null) {
                    if (response.isSuccessful()) {
                        listener.onSuccess();
                        assert response.body() != null;
                        Log.d(TAG, response.body().toString());
                    } else {
                        listener.onFail(response.message());
                        assert response.errorBody() != null;
                        try {
                            Log.e(TAG, "Error Body: " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.e(TAG, "Message: " + response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<Ticket> call, Throwable t) {
                if (listener != null)
                    listener.onConnectionError(t.getMessage());

                Log.e(TAG, "Connection Error: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    public void submitFeedbackWithAttachments(Context context, String subject, String body, List<String> reports, RequestListener listener) {
        TicketCompat ticketCompat = new TicketCompat();
        ticketCompat.setCustomer(Constants.ZAMMAD_CUSTOMER);
        ticketCompat.setGroup("Users");
        ticketCompat.setTitle(subject);
        List<ArticleAttachmentCompat> attachs = new ArrayList<>();
        reports.forEach(s -> {
            if (!s.isEmpty()) { //in case it returns empty for some reason
                String fileName = ScrubberUtils.writeReportToFile(context, s);
                String fileBase64 = FileUtils.getBase64(s.getBytes());
                attachs.add(new ArticleAttachmentCompat(fileName, fileBase64, Constants.MIME_TYPE_TEXT));
            } else {
                Log.d(TAG, "CrashReport is empty after scrub.");
            }
        });

        ticketCompat.setArticle(new TicketArticleCompat(subject, body, Constants.ARTICLE_TYPE, false, attachs));
        webServices.createTicket(getHeaderMap(), ticketCompat).enqueue(new Callback<Ticket>() {
            @Override
            public void onResponse(Call<Ticket> call, Response<Ticket> response) {
                if (listener != null) {
                    if (response.isSuccessful()) {
                        listener.onSuccess();
                        assert response.body() != null;
                        Log.d(TAG, response.body().toString());
                    } else {
                        listener.onFail(response.message());
                        assert response.errorBody() != null;
                        try {
                            Log.e(TAG, "Error Body: " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.e(TAG, "Message: " + response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<Ticket> call, Throwable t) {
                if (listener != null)
                    listener.onConnectionError(t.getMessage());

                Log.e(TAG, "Connection Error: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private Map<String, String> getHeaderMap() {
        Map<String, String> map = new HashMap<>();
        map.put(Constants.AUTHORIZATION, Constants.BEARER + Constants.TOKEN);
        map.put(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
        return map;
    }

    public String getLogcat() {
        StringBuilder log = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec("logcat -b all -d");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return insertCRs(log.toString());
    }

    /**
     * Breaks up the logcat generated into single lines by inserting carriage return at the proper places which is before
     * the dates.
     *
     * @param input string input
     * @return string output
     */
    private String insertCRs(String input) {
        String datePattern = "(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01]) ";
        Pattern pattern = Pattern.compile(datePattern);

        Matcher matcher = pattern.matcher(input);

        String out = input;
        Set<String> matchedWords = new HashSet<>();
        while (matcher.find()) {
            matchedWords.add(matcher.group());
        }

        for (String word : matchedWords) {
            out = out.replace(word, "\n" + word);
        }

        return out;
    }
}
