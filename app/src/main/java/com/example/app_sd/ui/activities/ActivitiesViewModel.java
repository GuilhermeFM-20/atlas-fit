package com.example.app_sd.ui.activities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ActivitiesViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ActivitiesViewModel() {
        mText = new MutableLiveData<>();

        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}