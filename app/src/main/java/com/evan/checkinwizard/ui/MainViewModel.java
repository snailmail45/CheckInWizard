package com.evan.checkinwizard.ui;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.evan.checkinwizard.data.model.AccessToken;
import com.evan.checkinwizard.data.model.CheckoutResult;
import com.evan.checkinwizard.data.MainRepo;
import com.evan.checkinwizard.data.model.Patient;
import com.evan.checkinwizard.data.model.Purchase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

@HiltViewModel
public class MainViewModel extends ViewModel {
    private final MainRepo mainRepo;
    private final MutableLiveData<AccessToken> accessTokenLiveData = new MutableLiveData<>();
    private final MutableLiveData<CheckoutResult> checkoutLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> imagesLiveData = new MutableLiveData<>();
    private final List<String> scannedImages = new ArrayList<>();
    private AccessToken accessToken;
    private CheckoutResult result;
    private String amount;
    private Patient patient;
    private String reason;
    private String patientId;

    @Inject
    public MainViewModel(MainRepo mainRepo) {
        this.mainRepo = mainRepo;
    }

    public void getToken() {
        mainRepo.getAccessToken().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(accessToken -> {
                    this.accessToken = accessToken;
                    Timber.d("Access token: " + accessToken.getClientToken());
                    accessTokenLiveData.setValue(accessToken);
                }, exception -> {
                    Timber.e("Failed to get access token: " + exception);
                });
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public String getPatientId() {
        return patientId;
    }

    public void makePurchase(Purchase purchase) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        patientId = uuid.substring(0, Math.min(uuid.length(), 8));
        mainRepo.checkout(purchase).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(checkoutResult -> {
                    this.result = checkoutResult;
                    checkoutLiveData.setValue(checkoutResult);
                }, exception -> {
                    Timber.e("Failed to purchase: " + exception);
                });
    }

    public CheckoutResult getResult() {
        return result;
    }

    public MutableLiveData<AccessToken> getAccessTokenLiveData() {
        return accessTokenLiveData;
    }

    public MutableLiveData<CheckoutResult> getCheckoutLiveData() {
        return checkoutLiveData;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAmount() {
        return amount;
    }

    public MainRepo getMainRepo() {
        return mainRepo;
    }

    public void addScannedImage(String scannedImagePath) {
        scannedImages.add(0, scannedImagePath);
        imagesLiveData.postValue(true);
    }

    public MutableLiveData<Boolean> getImagesLiveData() {
        return imagesLiveData;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Patient getPatient() {
        return patient;
    }

    public String getReason() {
        return reason;
    }

    public List<String> getScannedImages() {
        return scannedImages;
    }
}
