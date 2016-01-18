package com.bignerdranch.android.photogallery.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by zafer on 18.01.16.
 */
public class QueryPreferences {

    private static final String PREF_SEARCH_QUERY = "searchQuery";


    private static SharedPreferences getDefaultSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String getStoredQuery(Context context) {

        return getDefaultSharedPreferences(context).getString(PREF_SEARCH_QUERY, null);
    }


    public static void setStoredQuery(Context context, String query) {

        getDefaultSharedPreferences(context).edit().putString(PREF_SEARCH_QUERY, query).apply();
    }
}
