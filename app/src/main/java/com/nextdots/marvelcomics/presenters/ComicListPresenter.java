package com.nextdots.marvelcomics.presenters;

import android.accounts.NetworkErrorException;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.nextdots.marvelcomics.application.AppPreferences;
import com.nextdots.marvelcomics.di.component.AppComponent;
import com.nextdots.marvelcomics.di.component.DaggerAppComponent;
import com.nextdots.marvelcomics.fragments.ComicListFragment;
import com.nextdots.marvelcomics.rest.RestApi;
import com.nextdots.marvelcomics.rest.comics.ComicsResponse;
import com.nextdots.marvelcomics.rest.comics.Data;
import com.nextdots.marvelcomics.rest.comics.Result;
import com.nextdots.marvelcomics.utils.Constants;
import com.nextdots.marvelcomics.utils.Util;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Andrés Escobar on 19/02/2017.
 */

public class ComicListPresenter implements Callback<ComicsResponse> {

    @Inject
    RestApi restApi;

    @Inject
    AppPreferences appPreferences;

    private String TAG = ComicListFragment.class.getSimpleName();

    public ComicListPresenter(AppComponent appComponent) {
        appComponent.inject(this);
    }

    public void getComics(int ts, String apikey, String hash, int limit, Boolean offline) {
        if(offline){
            onFailure(null, new NetworkErrorException());
            return;
        }
        restApi.comics(Constants.TS, Constants.APIKEY, Constants.HASH, Constants.LIMIT).enqueue(this);
    }

    @Override
    public void onResponse(Call<ComicsResponse> call, Response<ComicsResponse> response) {
        if(response.isSuccessful() && response.body() != null){
            EventBus.getDefault().post(response.body());
        } else {
            onFailure(call, new NetworkErrorException());
        }
    }

    @Override
    public void onFailure(Call<ComicsResponse> call, Throwable t) {
        Log.e(TAG, "onFailure", t);

        String sComicListData = appPreferences.getPreference(Constants.COMIC_LIST_DATA, String.class);
        Type tComicListData = new TypeToken<HashMap<Integer, Result>>() {
        }.getType();
        HashMap<Integer, Result> comicListData
                = Util.getGson().fromJson(sComicListData, tComicListData);
        if (comicListData == null) {
            comicListData = new HashMap<Integer, Result>();
        }
        ComicsResponse comicsResponse = new ComicsResponse();
        comicsResponse.setData(new Data());
        comicsResponse.getData().setResults(new ArrayList<Result>());
        comicsResponse.getData().getResults().addAll(comicListData.values());
        EventBus.getDefault().post(comicsResponse);
    }
}
