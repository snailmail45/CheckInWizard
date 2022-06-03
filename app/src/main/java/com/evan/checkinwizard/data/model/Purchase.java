package com.evan.checkinwizard.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Purchase {
    @SerializedName("payment_method_nonce")
    @Expose
    private String payment_method_nonce;

    @SerializedName("amount_to_send")
    @Expose
    private String amount_to_send;

    private Purchase() {
    }

    public Purchase(String payment_method_nonce, String amount_to_send) {
        this.payment_method_nonce = payment_method_nonce;
        this.amount_to_send = amount_to_send;
    }

    public String getPayment_method_nonce() {
        return payment_method_nonce;
    }

    public String getAmount_to_send() {
        return amount_to_send;
    }
}
