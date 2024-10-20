package com.sishome.homeinventory.data.model

import com.google.gson.annotations.SerializedName

data class ProductosItem(
    val codigo_barra: String ="",
    val id: Int = 0,
    val nombre2: String ="",
    val observaciones: String ="",
    val precio_compra: String ="",
    val precio_venta: String ="",
    val producto: String =""
)