package com.example.dyna.LiveDataViews;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dyna.R;

public class CriticalForceLiveData extends BaseLiveDataView {

    //Effectively just static repeaters with a calculation at the end
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);

        return inflater.inflate(R.layout.critical_force_live_data_fragment,container, false);
    }

    @Override
    public void updateStats() {

    }
    @Override
    int getSaveButtonId() {
        //TODO IMPLEMENT
        return 0;
    }
}