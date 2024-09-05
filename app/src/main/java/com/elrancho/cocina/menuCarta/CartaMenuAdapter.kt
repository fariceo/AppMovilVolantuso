package com.elrancho.cocina.menuCarta

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.elrancho.cocina.R


class CartaMenuAdapter(private val productoCartas: List<ProductoCarta>) : RecyclerView.Adapter<CartaMenuAdapter.ProductoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_cartamenu, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productoCartas[position]
        holder.tvcartaProducto.text = producto.producto
        holder.tvcartaPrecio.text = "$ ${producto.precio.toString() }"// Asegúrate de convertir a String
    }

    override fun getItemCount() = productoCartas.size

    class ProductoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvcartaProducto: TextView = view.findViewById(R.id.tvcartaProducto)
        val tvcartaPrecio: TextView = view.findViewById(R.id.tvcartaPrecio)
    }
}
