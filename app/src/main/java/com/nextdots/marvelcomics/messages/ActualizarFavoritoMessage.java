package com.nextdots.marvelcomics.messages;

/**
 * Created by Andrés Escobar on 18/02/2017.
 */

public class ActualizarFavoritoMessage {

    private final Boolean favorito;

    public ActualizarFavoritoMessage(Boolean favorito) {
        this.favorito = favorito;
    }

    public Boolean isFavorito() {
        return favorito;
    }
}