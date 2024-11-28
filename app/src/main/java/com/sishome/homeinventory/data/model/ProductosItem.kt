package com.sishome.homeinventory.data.model

import com.google.gson.annotations.SerializedName

data class ProductosItem(
    @SerializedName("codigo_barras") val codigo_barra: String ="",
    @SerializedName("id") val id: Int = 0,
    @SerializedName("nombre2") val nombre2: String ="",
    @SerializedName("observaciones") val observaciones: String ="",
    @SerializedName("precio_compra") val precio_compra: String ="",
    @SerializedName("precio_venta") val precio_venta: String ="",
    @SerializedName("producto") val producto: String ="",
    @SerializedName("image") var image:String=""
)