package com.elrancho.cocina.consultaMysql

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.elrancho.cocina.R

class ProductoAdapter(private val productos: List<Producto>) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]
        holder.tvUsuario.text = producto.usuario
        holder.tvProducto.text = producto.producto
    }

    override fun getItemCount() = productos.size

    class ProductoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUsuario: TextView = view.findViewById(R.id.tvUsuario)
        val tvProducto: TextView = view.findViewById(R.id.tvProducto)
    }
}
