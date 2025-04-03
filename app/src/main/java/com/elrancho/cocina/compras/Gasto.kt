package com.elrancho.cocina.compras

data class Gasto(
    val id: Int,
    val producto: String,
    val precio: Double,
   // var seleccionado: Int = false,  // Puedes usarlo en el futuro si necesitas seleccionar varios gastos
    var estado: Int       // ✅ 1 = pagado, 0 = no pagado
)
