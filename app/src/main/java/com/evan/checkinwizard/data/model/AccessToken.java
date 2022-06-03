package com.evan.checkinwizard.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AccessToken {
    @SerializedName("clientToken")
    @Expose
    private String clientToken;

    public String getClientToken() {
        return clientToken;
    }
}
