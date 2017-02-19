package com.nextdots.marvelcomics.messages;

import android.os.Bundle;

import java.util.HashMap;

/**
 * Created by Andrés Escobar on 18/02/2017.
 */

public class FragmentMessage {

    public static final int FROM_SPLASH_TO_LOGIN = 1;
    public static final int FROM_SPLASH_TO_COMIC_LIST = 2;
    public static final int FROM_LOGIN_TO_COMIC_LIST = 3;
    public static final int LOGOFF = 4;
    public static final int FROM_COMIC_LIST_TO_DETALLE_COMIC = 5;
    public static final int FROM_NAV_FAVORITOS_TO_COMIC_LIST = 6;

    private final int fragmentAction;
    private final Bundle bundle;

    public FragmentMessage(int fragmentClass, Bundle bundle) {
        this.fragmentAction = fragmentClass;
        this.bundle = bundle;
    }

    public int getFragmentAction() {
        return fragmentAction;
    }

    public Bundle getBundle() {
        return bundle;
    }
}