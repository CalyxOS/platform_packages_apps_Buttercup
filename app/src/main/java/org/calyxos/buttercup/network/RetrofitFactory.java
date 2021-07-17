package org.calyxos.buttercup.network;


import org.calyxos.buttercup.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit factory class for building the Retrofit object
 * Created by Ese Udom on 7/15/2021.
 */
public class RetrofitFactory {
    private static Retrofit retrofit = null;

    private RetrofitFactory() {
        //class shouldn't be initialized
    }

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
