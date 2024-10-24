package com.sishome.homeinventory.data

import com.sishome.homeinventory.data.model.ProductosItem
import com.sishome.homeinventory.data.model.ProductosResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query


interface RetrofitService {

    /*
    @GET("users/{id}")
    suspend fun obtenerUsuario(
        @Path("id") id: Int
    ):UsersResultItem
    */

    @GET("api/productos")
    suspend fun obtenerProductos(
        @Query("producto") producto: String
    ): Response<ProductosResponse>

    @GET("api/productos/{id}")
    suspend fun obtenerProducto(
        @Path("id") id:Int
    ):Response<ProductosItem>

    @POST("api/productos")
    suspend fun crearProducto(
        @Body producto: ProductosItem
    ):Response<ProductosItem>

    @PUT("api/productos/{id}")
    suspend fun actualizarProducto(
        @Path("id") id:Int,
        @Body producto: ProductosItem
    ):Response<ProductosItem>

}

object RetrofitServiceFactory {
    fun makeRetrofitService(): RetrofitService {
        return Retrofit.Builder()
            .baseUrl("http://192.168.4.48:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitService::class.java)
    }
}