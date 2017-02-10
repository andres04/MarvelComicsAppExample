package com.nextdots.marvelcomics.utils;

import com.nextdots.marvelcomics.rest.ApiError;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by solerambp01 on 22/08/16.
 */
public class ErrorUtils {

    private Retrofit retrofit;

    public ErrorUtils(Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    public ApiError parseError(Response<?> response) {

        if(response.errorBody() == null){
            ApiError error = new ApiError();
            error.setMensaje("error");
            error.setStatus(-1);
        }

        Converter<ResponseBody, ApiError> converter = retrofit.responseBodyConverter(ApiError.class, new Annotation[0]);
        ApiError error;

        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new ApiError();
        }

        return error;
    }




}
