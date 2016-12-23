package com.example.shubham.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;

class Utility {

    public static boolean isOnline(@NonNull Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static void displayNoInternetMessage(@NonNull View view) {
        Snackbar.make(view, R.string.no_internet, Snackbar.LENGTH_LONG).show();
    }

    public static void displayError(@NonNull View view) {
        Snackbar.make(view, R.string.error, Snackbar.LENGTH_LONG).show();
    }

    public static String getSortType(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.pref_sortType_key),
                        context.getString(R.string.pref_sortTypes_default));
    }
}
