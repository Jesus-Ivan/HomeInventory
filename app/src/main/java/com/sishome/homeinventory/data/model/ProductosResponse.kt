package com.sishome.homeinventory.data.model

import com.google.gson.annotations.SerializedName

class ProductosResponse(
    @SerializedName("response") val response:String,
    @SerializedName("results") val products: List<ProductosItem>
)