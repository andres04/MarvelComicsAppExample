package com.nextdots.marvelcomics.application;

import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.nextdots.marvelcomics.di.component.AppComponent;
import com.nextdots.marvelcomics.di.component.DaggerAppComponent;
import com.nextdots.marvelcomics.di.module.AppModule;

public class App extends Application {

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        this.initializeInjector();
        this.initializeLeakDetection();
        this.initializeFacebookSdk();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    private void initializeFacebookSdk() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

    private void initializeLeakDetection() {
        //if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        //}
        //LeakCanary.install(this);
    }

    private void initializeInjector() {
        appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

}
