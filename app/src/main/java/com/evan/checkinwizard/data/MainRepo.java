package com.evan.checkinwizard.data;

import com.evan.checkinwizard.data.model.AccessToken;
import com.evan.checkinwizard.data.model.CheckoutResult;
import com.evan.checkinwizard.data.model.Purchase;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;

public class MainRepo {
    private final MainService mainService;

    @Inject
    public MainRepo(MainService mainService) {
        this.mainService = mainService;
    }

    public Single<AccessToken> getAccessToken(){
        return mainService.getAccessToken();
    }

    public Single<CheckoutResult> checkout(Purchase purchase){
        return mainService.checkout(purchase);
    }
}
