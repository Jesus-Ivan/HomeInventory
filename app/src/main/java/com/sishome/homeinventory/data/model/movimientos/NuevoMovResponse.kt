package com.sishome.homeinventory.data.model.movimientos

data class NuevoMovResponse(
    val message: String,
    val movimiento: MovimientoX,
    val ok: Boolean,
    val status: Int,
    val sumaMovimientos: Int
)