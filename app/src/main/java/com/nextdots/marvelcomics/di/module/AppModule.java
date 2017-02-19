package com.nextdots.marvelcomics.di.module;

import android.content.Context;
import android.content.SharedPreferences;

import com.nextdots.marvelcomics.application.App;
import com.nextdots.marvelcomics.application.AppPreferences;
import com.nextdots.marvelcomics.fragments.SplashFragment;
import com.nextdots.marvelcomics.presenters.ComicListPresenter;
import com.nextdots.marvelcomics.presenters.DetalleComicPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    App mApplication;

    public AppModule(App mApplication) {
        this.mApplication = mApplication;

    }

    @Provides
    @Singleton
    App provideApplication() {
        return mApplication;
    }



    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(){
        return mApplication.getSharedPreferences(AppPreferences.PREF_NAME, Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    AppPreferences provideAppPreferences(SharedPreferences preferences){
        return new AppPreferences(preferences);
    }

    @Provides
    @Singleton
    ComicListPresenter provideComicListPresenter(){
        return new ComicListPresenter(mApplication.getAppComponent());
    }

    @Provides
    @Singleton
    DetalleComicPresenter provideDetalleComicPrestenter(){
        return new DetalleComicPresenter(mApplication.getAppComponent());
    }

}
