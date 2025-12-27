package com.elrancho.cocina.productos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.elrancho.cocina.productos.puntoventa.ProductoCarrito
import com.elrancho.cocina.productos.puntoventa.PuntoVentaActivity
import com.elrancho.cocina.R


class ProductoDetalleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_producto_detalle)

        val btnAgregarPuntoVenta = findViewById<Button>(R.id.btnAgregarPuntoVenta)
        btnAgregarPuntoVenta.setOnClickListener {
            // Por ejemplo, agregar un producto de prueba al Punto de Venta
            val producto = ProductoCarrito(1, "Producto Demo", 1, 10.0)

            val intent = Intent(this, PuntoVentaActivity::class.java)
            intent.putExtra("producto_id", producto.id)
            intent.putExtra("producto_nombre", producto.nombre)
            intent.putExtra("producto_precio", producto.precio)
            startActivity(intent)
        }
    }
}
