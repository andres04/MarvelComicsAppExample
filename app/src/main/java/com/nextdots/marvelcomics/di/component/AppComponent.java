package com.nextdots.marvelcomics.di.component;

import com.nextdots.marvelcomics.activities.DetalleComicActivity;
import com.nextdots.marvelcomics.activities.LoginActivity;
import com.nextdots.marvelcomics.activities.MainActivity;
import com.nextdots.marvelcomics.activities.SplashActivity;
import com.nextdots.marvelcomics.application.App;
import com.nextdots.marvelcomics.di.module.AppModule;
import com.nextdots.marvelcomics.di.module.NetModule;
import com.nextdots.marvelcomics.fragments.ComicListFragment;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = {AppModule.class, NetModule.class})
public interface AppComponent {

    App getApplication();

    void inject(SplashActivity splashActivity);
    void inject(LoginActivity loginActivity);
    void inject(MainActivity mainActivity);
    void inject(DetalleComicActivity detalleComicActivity);
    void inject(ComicListFragment comicListFragment);

}
