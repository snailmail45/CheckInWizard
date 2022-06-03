package com.evan.checkinwizard.ui;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Boolean> scanLiveData = new MutableLiveData<>();

    public void scanDocument(){
        scanLiveData.setValue(true);
    }
    public MutableLiveData<Boolean> getScanLiveData() {
        return scanLiveData;
    }
}
