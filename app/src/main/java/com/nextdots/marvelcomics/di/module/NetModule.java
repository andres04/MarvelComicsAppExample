package com.nextdots.marvelcomics.di.module;

import com.nextdots.marvelcomics.rest.RestApi;
import com.nextdots.marvelcomics.utils.Constants;
import com.nextdots.marvelcomics.utils.ErrorUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


@Module
public class NetModule {

    @Provides
    OkHttpClient provideClient(){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
//        httpClient.authenticator(new MyAuthenticator());
        //client.setConnectTimeout(2, TimeUnit.MINUTES);
        // client.setReadTimeout(2, TimeUnit.MINUTES);
        return httpClient.build();

    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(OkHttpClient okHttpClient) {

        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }

    @Provides
    @Singleton
    RestApi provideResttApi(Retrofit retrofit){
        return retrofit.create(RestApi.class);
    }

    @Provides
    @Singleton
    ErrorUtils provideErrorUtils(Retrofit retrofit){
       return new ErrorUtils(retrofit);
    }

}
