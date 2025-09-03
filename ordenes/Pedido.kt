package com.elrancho.cocina.ordenes

data class Pedido(
    val producto: String,
    val cantidad: Int,
    val total: Double,
    val usuario: String,
    val fecha: String
)