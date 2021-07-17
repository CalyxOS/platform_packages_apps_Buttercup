package org.calyxos.buttercup.repo;

import android.util.Log;

import org.calyxos.buttercup.Constants;
import org.calyxos.buttercup.model.Ticket;
import org.calyxos.buttercup.model.compat.TicketArticleCompat;
import org.calyxos.buttercup.model.compat.TicketCompat;
import org.calyxos.buttercup.network.RequestListener;
import org.calyxos.buttercup.network.RetrofitFactory;
import org.calyxos.buttercup.network.WebServices;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
                if(listener != null)
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

}
