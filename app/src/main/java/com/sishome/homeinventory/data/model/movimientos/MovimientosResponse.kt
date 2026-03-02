package com.sishome.homeinventory.data.model.movimientos

data class MovimientosResponse(
    val count: Int,
    val movimientos: List<Movimiento>,
    val ok: Boolean,
    val status: Int
)