package com.elrancho.cocina.consultaMysql

data class Producto(
    val usuario: String,
    val producto: String,
    val cantidad:Int,
    val precio:Double,
    val total:Double,
    val metodo_pago:String,
    val fecha: String
)

/*
* consulta_activity.xml
* item_pedido_cliente
* */
