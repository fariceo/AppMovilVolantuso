package com.elrancho.cocina.ordenes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.elrancho.cocina.R

class ProductoAdapter(
    private val productos: List<Producto>,
    private val onProductoClick: (Producto) -> Unit
) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    inner class ProductoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.textViewNombre)
        val precio: TextView = view.findViewById(R.id.textViewPrecio)

        init {
            view.setOnClickListener {
                // Cuando el producto es clickeado, se invoca la función de callback
                onProductoClick(productos[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        // Inflar el layout del item del producto
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ingresar_producto, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]
        // Asignar los valores a los TextViews
        holder.nombre.text = producto.nombre
        holder.precio.text = "$${producto.precio}"
    }

    override fun getItemCount(): Int = productos.size
}