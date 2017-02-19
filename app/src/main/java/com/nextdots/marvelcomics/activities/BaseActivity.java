package com.nextdots.marvelcomics.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.nextdots.marvelcomics.application.App;
import com.nextdots.marvelcomics.di.component.AppComponent;

/**
 * Created by solerambp01 on 15/08/16.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupComponent(((App) getApplication()).getAppComponent());
    }

    protected void setupComponent(AppComponent appComponent){

    }

}
