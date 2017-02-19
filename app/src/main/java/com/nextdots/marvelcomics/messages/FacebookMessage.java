package com.nextdots.marvelcomics.messages;

import com.facebook.FacebookException;
import com.nextdots.marvelcomics.rest.entity.Perfil;

/**
 * Created by Andrés Escobar on 18/02/2017.
 */

public class FacebookMessage {

    public static final int ON_SUCCESS = 1;
    public static final int ON_ERROR = 2;
    public static final int ON_CANCEL = 3;

    private final int callbackManagerResponse;
    private final Perfil perfil;
    private final FacebookException facebookException;

    public FacebookMessage(int fragmentClass, Perfil perfil, FacebookException facebookException) {
        this.callbackManagerResponse = fragmentClass;
        this.perfil = perfil;
        this.facebookException = facebookException;
    }

    public int getCallbackManagerResponse() {
        return callbackManagerResponse;
    }

    public Perfil getPerfil() {
        return perfil;
    }
}