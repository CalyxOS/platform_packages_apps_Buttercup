package org.calyxos.buttercup.network;

import org.calyxos.buttercup.model.Ticket;
import org.calyxos.buttercup.model.compat.TicketCompat;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

/**
 * Web service interface. Contains all web service calls
 * Created by Ese Udom on 7/14/2021.
 */
public interface WebServices {

    @POST("/api/v1/tickets")
    Call<Ticket> createTicket(@HeaderMap Map<String, String> headerMap, @Body TicketCompat body);
}

