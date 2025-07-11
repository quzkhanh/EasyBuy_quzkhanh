package com.example.easybuy.network;

import com.example.easybuy.model.Product;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface ProductApi {

    @GET("products")
    Call<List<Product>> getAllProducts();

    @GET("products/{id}")
    Call<Product> getProductById(@Path("id") String id);

    @GET("products")
    Call<List<Product>> getProductsByAdmin(@Query("adminId") int adminId);

    @POST("products")
    Call<Product> addProduct(@Body Product product);

    @PUT("products/{id}")
    Call<Product> updateProduct(@Path("id") String id, @Body Product product);

    @DELETE("products/{id}")
    Call<Void> deleteProduct(@Path("id") String id);
}
