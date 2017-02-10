package com.nextdots.marvelcomics.rest;

import com.nextdots.marvelcomics.rest.comic.ComicResponse;
import com.nextdots.marvelcomics.rest.comics.ComicsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RestApi {

    @GET("comics")
    Call<ComicsResponse> comics(@Query("ts")Integer ts, @Query("apikey")String apikey, @Query("hash")String hash, @Query("limit")Integer limit);

    @GET("comics/{id}")
    Call<ComicResponse> comic(@Path("id") int id, @Query("ts")Integer ts, @Query("apikey")String apikey, @Query("hash")String hash);
    //@Path("id") int groupId

}
