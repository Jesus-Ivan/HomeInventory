package com.sishome.homeinventory.customers

data class CustomersResponse(
    val count: Int,
    val list: List<Item0>,
    val ok: Boolean,
    val status: Int
)