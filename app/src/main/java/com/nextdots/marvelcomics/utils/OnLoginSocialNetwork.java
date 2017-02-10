package com.nextdots.marvelcomics.utils;

import com.nextdots.marvelcomics.rest.entity.Perfil;

/**
 * Created by solerambp01 on 27/10/16.
 */

public interface OnLoginSocialNetwork {

    void onSuccess(Perfil perfil);
    void onError();
}
