package com.example.capsuletime.core.interceptor;

import android.content.Context;
import android.util.Log;

import com.example.capsuletime.core.preferences.CookieSharedPreferences;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;

import okhttp3.Interceptor;
import okhttp3.Response;

public class ReceivedCookiesInterceptor implements Interceptor {

    private CookieSharedPreferences cookieSharedPreferences;

    public ReceivedCookiesInterceptor(Context context){
        cookieSharedPreferences = CookieSharedPreferences.getInstanceOf(context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Response response = chain.proceed(chain.request());

        if (!Objects.equals(response.header("Set-Cookie", "noCookie"), "noCookie")){
            HashSet<String> cookies = new HashSet<>();

            for (String header : response.headers("Set-Cookie")){
                Log.d("Cookie", header.toString());
                cookies.add(header);
            }
            cookieSharedPreferences.putHashSet(CookieSharedPreferences.COOKIE_SHARED_PREFERENCES_KEY, cookies);

        }

        return response;
    }
}