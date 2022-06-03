package com.evan.checkinwizard.data;

import com.evan.checkinwizard.data.model.AccessToken;
import com.evan.checkinwizard.data.model.CheckoutResult;
import com.evan.checkinwizard.data.model.Purchase;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface MainService {
    @GET("access-token")
    Single<AccessToken> getAccessToken();

    @POST("checkout")
    Single<CheckoutResult> checkout(@Body Purchase purchase);
}
