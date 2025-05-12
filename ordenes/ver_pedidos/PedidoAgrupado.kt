package com.elrancho.cocina.ordenes.ver_pedidos

data class PedidoAgrupado(
    val id: Int,
    val usuario: String,
    val total: Double,
    var fecha: String,
    val productos: List<DetalleProducto>,
    var metodo_pago: String // 👈 nuevo campo
)




/*
* Archivos esenciales para el funcionamiento de "VerPedidosActivity"
* ----folder Ver_pedidos----
* VerPedidosActivity.kt
* PedidoAgrupadoAdapter.kt
* PedidoAgrupado.kt
* DetalleProducto.kt
*
* ---Folder Res/layout
* item_pedido_agrupado.xml
* item_detalle_producto.xml
* activity_ver_pedidos.xml
* pedidos_agrupados.php
* */