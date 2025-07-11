package com.example.easybuy.network;

import com.example.easybuy.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

import java.util.HashMap;

public interface UserApi {

    @POST("users/login")
    Call<HashMap<String, Object>> loginUser(@Body HashMap<String, String> loginRequest);

    @POST("users/register")
    Call<HashMap<String, Object>> registerUser(@Body User user);

    @PUT("users/{id}")
    Call<HashMap<String, Object>> updateUser(@Path("id") int id, @Body User user);

    @DELETE("users/{id}")
    Call<HashMap<String, Object>> deleteUser(@Path("id") int id);
}
