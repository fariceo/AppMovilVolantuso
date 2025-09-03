package com.elrancho.cocina.ordenes.ver_pedidos

data class DetalleProducto(
    val producto: String,
    val cantidad: Int,
    val precio: Double,
    val total: Double,
    val fecha: String,
    val delivery_type: String,  // Tipo de delivery
    var delivery_cost: Double   // Costo de delivery calculado
)