package com.elrancho.cocina.productos.puntoventa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.elrancho.cocina.R

class CarritoAdapter(
    private val items: List<ProductoCarrito>,
    private val onItemChange: (ProductoCarrito) -> Unit,
    private val onItemRemove: (ProductoCarrito) -> Unit
) : RecyclerView.Adapter<CarritoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto_carrito, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = items[position]
        holder.nombre.text = producto.nombre
        holder.precio.text = "💲${producto.precio}"
        holder.cantidad.text = "x${producto.cantidad}"

        holder.btnEditar.setOnClickListener {
            val context = holder.itemView.context
            val input = android.widget.EditText(context)
            input.setText(producto.cantidad.toString())
            AlertDialog.Builder(context)
                .setTitle("Editar cantidad")
                .setView(input)
                .setPositiveButton("OK") { _, _ ->
                    val nuevaCant = input.text.toString().toIntOrNull()
                    if (nuevaCant != null && nuevaCant > 0) {
                        producto.cantidad = nuevaCant
                        notifyItemChanged(position)
                        onItemChange(producto)
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        holder.btnEliminar.setOnClickListener {
            onItemRemove(producto)
        }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.txtNombreProducto)
        val precio: TextView = view.findViewById(R.id.txtPrecioProducto)
        val cantidad: TextView = view.findViewById(R.id.txtCantidad)
        val btnEditar: Button = view.findViewById(R.id.btnEditar)
        val btnEliminar: Button = view.findViewById(R.id.btnEliminar)
    }
}
