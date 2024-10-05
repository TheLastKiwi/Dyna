package com.example.dyna;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CriticalForceLiveData extends BaseLiveDataView {

    //Effectively just static repeaters with a calculation at the end
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);

        return inflater.inflate(R.layout.activity_critical_force_live_data,container, false);
    }

    @Override
    public void updateStats() {

    }
}