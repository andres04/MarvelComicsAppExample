package com.nextdots.marvelcomics.messages;

import android.support.v7.widget.Toolbar;

/**
 * Created by Andrés Escobar on 18/02/2017.
 */

public class ObtenerFavoritoMessage {

    private final Boolean favorito;

    public ObtenerFavoritoMessage(Boolean favorito) {
        this.favorito = favorito;
    }

    public Boolean isFavorito() {
        return favorito;
    }
}