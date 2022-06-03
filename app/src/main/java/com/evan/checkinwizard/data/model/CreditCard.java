package com.evan.checkinwizard.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreditCard {
    @SerializedName("last4")
    @Expose
    private String lastFour;

    @SerializedName("cardType")
    @Expose
    private String cardType;

    public String getLastFour() {
        return lastFour;
    }

    public String getCardType() {
        return cardType;
    }

    @Override
    public String toString() {
        return "CreditCard{" +
                "lastFour='" + lastFour + '\'' +
                ", cardType='" + cardType + '\'' +
                '}';
    }
}
