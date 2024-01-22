package com.example.mobil5.ui.addlabel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddLabelViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AddLabelViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is add label fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
