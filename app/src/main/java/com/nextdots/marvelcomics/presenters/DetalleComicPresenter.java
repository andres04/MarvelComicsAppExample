package com.nextdots.marvelcomics.presenters;

import android.accounts.NetworkErrorException;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.nextdots.marvelcomics.application.AppPreferences;
import com.nextdots.marvelcomics.di.component.AppComponent;
import com.nextdots.marvelcomics.messages.ActualizarFavoritoMessage;
import com.nextdots.marvelcomics.messages.ObtenerFavoritoMessage;
import com.nextdots.marvelcomics.rest.RestApi;
import com.nextdots.marvelcomics.rest.comic.ComicResponse;
import com.nextdots.marvelcomics.rest.comic.Result;
import com.nextdots.marvelcomics.utils.Constants;
import com.nextdots.marvelcomics.utils.Util;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.HashMap;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Andrés Escobar on 19/02/2017.
 */

public class DetalleComicPresenter implements Callback<ComicResponse> {

    private String TAG = DetalleComicPresenter.class.getSimpleName();

    @Inject
    RestApi restApi;

    @Inject
    AppPreferences appPreferences;

    private int idComic;

    public DetalleComicPresenter(AppComponent appComponent) {
        appComponent.inject(this);
    }


    public void comic(Integer idComic, int ts, String apikey, String hash) {
        this.idComic = idComic;
        restApi.comic(idComic,ts,apikey,hash).enqueue(this);
    }

    @Override
    public void onResponse(Call<ComicResponse> call, Response<ComicResponse> response) {
        if (response.isSuccessful() && response.body() != null
                && response.body().getData() != null
                && response.body().getData().getResults()!= null
                && !response.body().getData().getResults().isEmpty()) {
            ComicResponse comicResponse = response.body();
            EventBus.getDefault().post(comicResponse.getData().getResults().get(0));
        } else {
            onFailure(call, new NetworkErrorException(Constants.NETWORK_ERROR_MESSAGE));
        }
    }

    @Override
    public void onFailure(Call<ComicResponse> call, Throwable t) {
        Log.e(TAG, "onFailure", t);
        String sComicData = appPreferences.getPreference(Constants.COMIC_DATA, String.class);
        Type tComicData = new TypeToken<HashMap<Integer, Result>>() {
        }.getType();
        HashMap<Integer, Result> comicData
                = Util.getGson().fromJson(sComicData, tComicData);
        if (comicData != null) {
            Result result = comicData.get(idComic);
            if (result != null) {
                EventBus.getDefault().post(result);
            } else {
                EventBus.getDefault().post(t);
            }
        } else {
            EventBus.getDefault().post(t);
        }
    }

    public void obtenerFavorito(){
        String sComicListData = appPreferences.getPreference(Constants.COMIC_LIST_DATA, String.class);
        Type tComicListData = new TypeToken<HashMap<Integer, com.nextdots.marvelcomics.rest.comics.Result>>() {
        }.getType();
        HashMap<Integer, com.nextdots.marvelcomics.rest.comics.Result> comicListData
                = Util.getGson().fromJson(sComicListData, tComicListData);
        if (comicListData == null) {
            comicListData = new HashMap<Integer, com.nextdots.marvelcomics.rest.comics.Result>();
        }
        com.nextdots.marvelcomics.rest.comics.Result comicsResult = comicListData.get(idComic);
        EventBus.getDefault().post(new ObtenerFavoritoMessage(comicsResult != null));
    }

    public void actualizarFavorito(String comicsListResultItem, Result result) {
        String sComicListData = appPreferences.getPreference(Constants.COMIC_LIST_DATA, String.class);
        String sComicData = appPreferences.getPreference(Constants.COMIC_DATA, String.class);

        Type tComicListData = new TypeToken<HashMap<Integer, com.nextdots.marvelcomics.rest.comics.Result>>() {
        }.getType();
        Type tComicData = new TypeToken<HashMap<Integer, Result>>() {
        }.getType();

        HashMap<Integer, com.nextdots.marvelcomics.rest.comics.Result> comicListData
                = Util.getGson().fromJson(sComicListData, tComicListData);
        HashMap<Integer, Result> comicData
                = Util.getGson().fromJson(sComicData, tComicData);

        if (comicListData == null) {
            comicListData = new HashMap<Integer, com.nextdots.marvelcomics.rest.comics.Result>();
        }
        if (comicData == null) {
            comicData = new HashMap<Integer, Result>();
        }

        com.nextdots.marvelcomics.rest.comics.Result comicsResult = comicListData.get(idComic);
        if (comicsResult == null) {
            comicListData.put(idComic, Util.getGson().fromJson(comicsListResultItem
                    , com.nextdots.marvelcomics.rest.comics.Result.class));
            comicData.put(idComic, result);

            appPreferences.savePreference(Constants.COMIC_LIST_DATA, Util.getGson().toJson(comicListData));
            appPreferences.savePreference(Constants.COMIC_DATA, Util.getGson().toJson(comicData));

        } else {
            comicListData.remove(idComic);
            comicData.remove(idComic);

            appPreferences.savePreference(Constants.COMIC_LIST_DATA, Util.getGson().toJson(comicListData));
            appPreferences.savePreference(Constants.COMIC_DATA, Util.getGson().toJson(comicData));
        }

        EventBus.getDefault().post(new ActualizarFavoritoMessage(comicsResult == null));

    }

}
