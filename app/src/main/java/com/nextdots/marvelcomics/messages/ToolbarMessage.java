package com.nextdots.marvelcomics.messages;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

/**
 * Created by Andrés Escobar on 18/02/2017.
 */

public class ToolbarMessage {

    private final Toolbar toolbar;

    public ToolbarMessage(Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }
}