package com.evan.checkinwizard.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Transaction implements Serializable {
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("amount")
    @Expose
    private String amount;

    @SerializedName("createdAt")
    @Expose
    private String createdAt;

    @SerializedName("merchantAccountId")
    @Expose
    private String merchantAccountId;

    @SerializedName("creditCard")
    @Expose
    private CreditCard creditCard;


    public String getId() {
        return id;
    }

    public String getAmount() {
        return amount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getMerchantAccountId() {
        return merchantAccountId;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", amount='" + amount + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", merchantAccountId='" + merchantAccountId + '\'' +
                '}';
    }
}
