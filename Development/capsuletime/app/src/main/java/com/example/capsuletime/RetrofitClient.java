package com.example.capsuletime;

import android.content.Context;

import com.example.capsuletime.core.interceptor.AddCookiesInterceptor;
import com.example.capsuletime.core.interceptor.ReceivedCookiesInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    //객체생성

    String URL = "http://121.135.70.3:7070";
    //String URL = "http://59.13.134.140:7070";
    public RetrofitInterface retrofitInterface;
    public RetrofitClient(Context context) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new ReceivedCookiesInterceptor(context))
                .addNetworkInterceptor(new AddCookiesInterceptor(context))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);
    }
}
