package com.elrancho.cocina.ordenes.ver_pedidos

data class DetalleProducto(
    val producto: String,
    val cantidad: Int,
    val precio: Double,
    val total: Double,
    val fecha: String
)
