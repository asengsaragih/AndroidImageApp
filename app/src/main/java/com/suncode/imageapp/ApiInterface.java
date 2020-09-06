package com.suncode.imageapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("/v2/list")
    public Call<List<Model>> getImage(@Query("page") int page, @Query("limit") int limit);
}
