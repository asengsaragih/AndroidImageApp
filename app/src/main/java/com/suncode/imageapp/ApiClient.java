package com.suncode.imageapp;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static Retrofit build() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://picsum.photos/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        return retrofit;
    }
}
