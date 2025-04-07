package com.elrancho.cocina.ordenes.ver_pedidos

data class PedidoAgrupado(
    val usuario: String,
    val total: Double,
    val fecha: String,
    val productos: List<DetalleProducto>
)
