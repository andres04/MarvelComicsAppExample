package com.nextdots.marvelcomics.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.nextdots.marvelcomics.R;
import com.nextdots.marvelcomics.activities.MainActivity;
import com.nextdots.marvelcomics.application.AppPreferences;
import com.nextdots.marvelcomics.di.component.AppComponent;
import com.nextdots.marvelcomics.messages.FacebookMessage;
import com.nextdots.marvelcomics.messages.FragmentMessage;
import com.nextdots.marvelcomics.rest.RestApi;
import com.nextdots.marvelcomics.utils.Constants;
import com.nextdots.marvelcomics.presenters.FacebookPresenter;
import com.nextdots.marvelcomics.utils.Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import mehdi.sakout.dynamicbox.DynamicBox;

public class LoginFragment extends BaseFragment{
    private View view;

    @BindView(R.id.rl_content)
    RelativeLayout rlContent;
    @BindView(R.id.ivFondo)
    ImageView ivFondo;
    @BindView(R.id.btnEntrarConFacebook)
    LinearLayout btnEntrarConFacebook;

    private FacebookPresenter facebookPresenter;
    private DynamicBox dynamicBox;

    @Inject
    RestApi restApi;
    @Inject
    AppPreferences appPreferences;
    private Unbinder bind;

    @Override
    protected void setupComponent(AppComponent appComponent) {
        appComponent.inject(this);
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_login, container, false);
        bind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        facebookPresenter = new FacebookPresenter();
        dynamicBox = new DynamicBox(getActivity(), rlContent);
        Glide.with(this).load(R.drawable.marvel_comics).centerCrop().into(ivFondo);
    }

    @OnClick(R.id.btnEntrarConFacebook)
    void onClickFacebook() {
        if (Util.isNetworkConnected(getActivity())) {
            dynamicBox.showLoadingLayout();
            facebookPresenter.login(getActivity());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == MainActivity.RESULT_OK) {
            btnEntrarConFacebook.setVisibility(View.GONE);
            facebookPresenter.getCallBackManager().onActivityResult(requestCode, resultCode, data);
        } else {
            dynamicBox.hideAll();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(FacebookMessage event){
        switch (event.getCallbackManagerResponse()){
            case FacebookMessage.ON_SUCCESS:
                appPreferences.savePreference(Constants.PERFIL, Util.getGson().toJson(event.getPerfil()));
                dynamicBox.hideAll();
                EventBus.getDefault().post(new FragmentMessage(FragmentMessage.FROM_LOGIN_TO_COMIC_LIST, null));
                break;
            case FacebookMessage.ON_ERROR:
                dynamicBox.hideAll();
                Util.showMessageDialog(getString(R.string.http_error_message), getActivity());
                break;
            case FacebookMessage.ON_CANCEL:
                dynamicBox.hideAll();
                break;
        }
    }

    @Override
    public void onDestroy() {
        bind.unbind();
        super.onDestroy();
    }

}
