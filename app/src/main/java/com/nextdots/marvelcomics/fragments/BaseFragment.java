package com.nextdots.marvelcomics.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.google.gson.Gson;
import com.nextdots.marvelcomics.application.App;
import com.nextdots.marvelcomics.di.component.AppComponent;

/**
 * Created by solerambp01 on 23/08/16.
 */
public abstract class BaseFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupComponent(((App) getActivity().getApplication()).getAppComponent());
    }

    protected void setupComponent(AppComponent appComponent){

    }

}
