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

public class LoginActivity extends BaseActivity implements OnLoginSocialNetwork {

    @BindView(R.id.activity_main)
    RelativeLayout activityMain;
    @BindView(R.id.ivFondo)
    ImageView ivFondo;
    @BindView(R.id.btnEntrarConFacebook)
    LinearLayout btnEntrarConFacebook;

    private FacebookProvider mFacebook;
    private DynamicBox dynamicBox;

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
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mFacebook = new FacebookProvider(this);
        dynamicBox = new DynamicBox(this, activityMain);
        Glide.with(this).load(R.drawable.marvel_comics).centerCrop().into(ivFondo);

    }

    @OnClick(R.id.btnEntrarConFacebook)
    void onClickFacebook() {
        if (Util.isNetworkConnected(this)) {
            dynamicBox.showLoadingLayout();
            mFacebook.login(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            btnEntrarConFacebook.setVisibility(View.GONE);
            mFacebook.getCallBackManager().onActivityResult(requestCode, resultCode, data);
        } else {
            dynamicBox.hideAll();
        }
    }

    @Override
    public void onSuccess(Perfil perfil) {
        appPreferences.savePreference(Constants.PERFIL, gson.toJson(perfil));
        dynamicBox.hideAll();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onError() {
        dynamicBox.hideAll();
        Util.showMessageDialog("En estos momentos no cuentas con conexión a internet. Por favor, inténtalo más tarde.", this);
    }


}
