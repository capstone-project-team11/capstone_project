package com.example.capsuletime.core.interceptor;

import android.content.Context;
import android.util.Log;

import com.example.capsuletime.core.preferences.CookieSharedPreferences;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AddCookiesInterceptor implements Interceptor {

    private CookieSharedPreferences cookieSharedPreferences;

    public AddCookiesInterceptor(Context context){
        cookieSharedPreferences = CookieSharedPreferences.getInstanceOf(context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request.Builder builder = chain.request().newBuilder();

        HashSet<String> cookies = (HashSet<String>) cookieSharedPreferences.getHashSet(
                CookieSharedPreferences.COOKIE_SHARED_PREFERENCES_KEY,
                new HashSet<String>()
        );
        for (String cookie : cookies) {
            builder.addHeader("Cookie", cookie);
        }

        return chain.proceed(builder.build());
    }
}
