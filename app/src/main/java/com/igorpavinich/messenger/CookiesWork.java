package com.igorpavinich.messenger;

import android.content.SharedPreferences;

/**
 * Created by Igor Pavinich on 29.11.2017.
 */

public class CookiesWork {
    static final String COOKIES_HEADER = "Set-Cookie";
    static String cookie;

    static void saveCookie(SharedPreferences preferences){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("cookie", cookie);
        editor.apply();
    }

    static void loadCookie(SharedPreferences preferences) {
        cookie = preferences.getString("cookie", "");
    }

}
