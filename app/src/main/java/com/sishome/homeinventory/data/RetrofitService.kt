package com.sishome.homeinventory.data

import com.sishome.homeinventory.data.model.Productos
import com.sishome.homeinventory.data.model.ProductosItem
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
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
    ): Productos

    @GET("api/productos/{codigo_barra}")
    suspend fun obtenerProducto(): ProductosItem

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