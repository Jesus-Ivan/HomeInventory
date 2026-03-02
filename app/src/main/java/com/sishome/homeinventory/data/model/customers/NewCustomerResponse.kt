package com.sishome.homeinventory.data.model.customers

data class NewCustomerResponse(
    val customer: Customer,
    val message: String,
    val ok: Boolean,
    val status: Int
)