package com.nextdots.marvelcomics.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.nextdots.marvelcomics.R;
import com.nextdots.marvelcomics.application.AppPreferences;
import com.nextdots.marvelcomics.di.component.AppComponent;
import com.nextdots.marvelcomics.messages.FragmentMessage;
import com.nextdots.marvelcomics.rest.RestApi;
import com.nextdots.marvelcomics.rest.entity.Perfil;
import com.nextdots.marvelcomics.utils.Constants;
import com.nextdots.marvelcomics.utils.Util;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SplashFragment extends BaseFragment {

    @BindView(R.id.ivFondo)
    ImageView ivFondo;

    @Inject
    RestApi restApi;
    @Inject
    AppPreferences appPreferences;
    private Unbinder bind;

    @Override
    protected void setupComponent(AppComponent appComponent) {
        appComponent.inject(this);
    }

    private View view;

    public SplashFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SplashFragment newInstance() {
        return new SplashFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_splash, container, false);
        bind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivFondo.postDelayed(new Runnable() {
            @Override
            public void run() {
                Perfil perfil = Util.getGson().fromJson(appPreferences.getPreference(Constants.PERFIL, String.class), Perfil.class);
                if(perfil == null) {
                    EventBus.getDefault().post(new FragmentMessage(FragmentMessage.FROM_SPLASH_TO_LOGIN, null));
                } else {
                    EventBus.getDefault().post(new FragmentMessage(FragmentMessage.FROM_SPLASH_TO_COMIC_LIST, null));
                }
            }
        }, 1000);
        Glide.with(this).load(R.drawable.marvel_comics).centerCrop().into(ivFondo);
    }

    @Override
    public void onDestroy() {
        bind.unbind();
        super.onDestroy();
    }

}
