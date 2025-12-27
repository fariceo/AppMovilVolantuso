package com.elrancho.cocina.productos.puntoventa

data class ProductoCarrito(
    val id: Int,
    val nombre: String,
    var cantidad: Int,
    val precio: Double
)
