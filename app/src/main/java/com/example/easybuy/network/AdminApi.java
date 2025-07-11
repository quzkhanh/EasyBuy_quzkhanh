package com.example.easybuy.network;

import com.example.easybuy.model.Admin;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.util.Map;

public interface AdminApi {

    @POST("auth/login")
    Call<Map<String, Object>> login(@Body Map<String, String> credentials);

    @POST("auth/register")
    Call<Map<String, Object>> register(@Body Map<String, String> body);
}
