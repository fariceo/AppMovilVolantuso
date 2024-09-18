package com.elrancho.cocina.menuCarta

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.elrancho.cocina.R


class CartaMenuAdapter(

    private val productoCartas: List<ProductoCarta>) : RecyclerView.Adapter<CartaMenuAdapter.ProductoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_cartamenu, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productoCartas[position]
        holder.tvcartaProducto.text = producto.producto
        holder.tvcartaPrecio.text = "$ ${producto.precio.toString() }"// Asegúrate de convertir a String

        // Manejo del clic en el botón agregar_producto
        holder.agregarProducto.setOnClickListener {
            mostrarAlerta(holder.itemView.context, producto)
        }

    }

    override fun getItemCount() = productoCartas.size
    // Mostrar alerta cuando se hace clic en el botón
    private fun mostrarAlerta(context: android.content.Context, producto: ProductoCarta) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Agregar producto")
        builder.setMessage("Has agregado: ${producto.producto} \nPrecio: $${producto.precio}")
        builder.setPositiveButton("aceptar") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }
    class ProductoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvcartaProducto: TextView = view.findViewById(R.id.tvcartaProducto)
        val tvcartaPrecio: TextView = view.findViewById(R.id.tvcartaPrecio)
        val agregarProducto: Button = view.findViewById(R.id.agregar_producto)
    }
}
