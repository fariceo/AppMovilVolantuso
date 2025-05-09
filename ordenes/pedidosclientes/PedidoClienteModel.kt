package com.elrancho.cocina.ordenes.pedidosclientes

data class PedidoClienteModel(
    val producto: String,
    val cantidad: Int,
    val precio: Double,
    val total: Double
)
