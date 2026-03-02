package com.sishome.homeinventory.data.model.movimientos

data class Movimiento(
    val ClienteId: String,
    val clienteId: String,
    val concepto: String,
    val createdAt: String,
    val estado: String,
    val fecha: String,
    val id: Int,
    val img_url: Any,
    val monto: String,
    val updatedAt: String
)