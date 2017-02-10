package com.nextdots.marvelcomics.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.nextdots.marvelcomics.R;
import com.nextdots.marvelcomics.application.AppPreferences;
import com.nextdots.marvelcomics.di.component.AppComponent;
import com.nextdots.marvelcomics.rest.RestApi;
import com.nextdots.marvelcomics.rest.entity.Perfil;
import com.nextdots.marvelcomics.utils.Constants;
import com.nextdots.marvelcomics.utils.FacebookProvider;
import com.nextdots.marvelcomics.utils.OnLoginSocialNetwork;
import com.nextdots.marvelcomics.utils.Util;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mehdi.sakout.dynamicbox.DynamicBox;

public class SplashActivity extends BaseActivity {

    @BindView(R.id.ivFondo)
    ImageView ivFondo;


    @Inject
    RestApi restApi;
    @Inject
    AppPreferences appPreferences;

    @Override
    protected void setupComponent(AppComponent appComponent) {
        appComponent.inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        final Perfil perfil = gson.fromJson(appPreferences.getPreference(Constants.PERFIL, String.class), Perfil.class);

        ivFondo.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = null;
                if(perfil == null) {
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 1000);
        Glide.with(this).load(R.drawable.marvel_comics).centerCrop().into(ivFondo);

    }


}
