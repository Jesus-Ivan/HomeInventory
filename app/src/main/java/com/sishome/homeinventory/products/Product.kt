package com.sishome.homeinventory.products

data class Product(
    var id: Int,
    var producto: String,
    var nombre2: String = "",
    var precio_compra: Float = 0.0f,
    var precio_venta: Float = 0.0f,
    var codigo_barra: String = "",
    var observaciones: String = ""
) {
}