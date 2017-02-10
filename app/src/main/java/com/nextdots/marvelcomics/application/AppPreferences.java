package com.nextdots.marvelcomics.application;

import android.content.SharedPreferences;

public class AppPreferences {

    public static final String PREF_NAME = "marvel";
    private SharedPreferences sharedPreferences;

    public static String PREF_LOGGED = "logged";


    public AppPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public <T extends Object> T getPreference(final String prefKey, Class<T> classs) {
        if (classs == String.class) {
            return classs.cast(sharedPreferences.getString(prefKey, null));
        } else if (classs == Boolean.class) {
            return classs.cast(sharedPreferences.getBoolean(prefKey, false));
        } else if (classs == Integer.class) {
            return classs.cast(sharedPreferences.getInt(prefKey, -1));
        } else if (classs == Long.class) {
            return classs.cast(sharedPreferences.getLong(prefKey, -1));
        } else if (classs == Float.class) {
            return classs.cast(sharedPreferences.getFloat(prefKey, -1));
        }
        return null;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public void savePreference(final String strPrefKey, final Object objPrefValue) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (objPrefValue instanceof String) {
            editor.putString(strPrefKey, (String) objPrefValue);
        } else if (objPrefValue instanceof Boolean) {
            editor.putBoolean(strPrefKey, (Boolean) objPrefValue);
        } else if (objPrefValue instanceof Integer) {
            editor.putInt(strPrefKey, (Integer) objPrefValue);
        } else if (objPrefValue instanceof Long) {
            editor.putLong(strPrefKey, (Long) objPrefValue);
        } else if (objPrefValue instanceof Float) {
            editor.putFloat(strPrefKey, (Float) objPrefValue);
        }
        editor.commit();
    }


    public void clean() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();

    }


    public boolean isLogged() {
        return sharedPreferences.getBoolean(PREF_LOGGED, false);
    }


}
