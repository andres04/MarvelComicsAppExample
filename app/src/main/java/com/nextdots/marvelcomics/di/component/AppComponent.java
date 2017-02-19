package com.nextdots.marvelcomics.di.component;

import com.nextdots.marvelcomics.activities.MainActivity;
import com.nextdots.marvelcomics.application.App;
import com.nextdots.marvelcomics.di.module.AppModule;
import com.nextdots.marvelcomics.di.module.NetModule;
import com.nextdots.marvelcomics.fragments.ComicListFragment;
import com.nextdots.marvelcomics.fragments.DetalleComicFragment;
import com.nextdots.marvelcomics.fragments.LoginFragment;
import com.nextdots.marvelcomics.fragments.SplashFragment;
import com.nextdots.marvelcomics.presenters.ComicListPresenter;
import com.nextdots.marvelcomics.presenters.DetalleComicPresenter;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = {AppModule.class, NetModule.class})
public interface AppComponent {

    App getApplication();
    void inject(MainActivity mainActivity);

    void inject(SplashFragment splashFragment);
    void inject(ComicListFragment comicListFragment);
    void inject(LoginFragment loginFragment);
    void inject(DetalleComicFragment detalleComicFragment);
    void inject(DetalleComicPresenter detalleComicPresenter);
    void inject(ComicListPresenter comicListPresenter);

}
