package com.sishome.homeinventory.data.model.customers.edit

data class CustomerResponse(
    val ok: Boolean,
    val result: List<Result>,
    val status: Int
)