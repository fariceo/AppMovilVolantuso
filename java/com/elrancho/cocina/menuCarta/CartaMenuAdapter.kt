package com.elrancho.cocina.menuCarta

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.elrancho.cocina.R
import android.widget.Toast
import android.content.Context


class CartaMenuAdapter(private val productos: List<ProductoCarta>) :
    RecyclerView.Adapter<CartaMenuAdapter.CartaMenuViewHolder>() {

    class CartaMenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagenProducto: ImageView = itemView.findViewById(R.id.imagenProducto)
        val tvProducto: TextView = itemView.findViewById(R.id.tvcartaProducto)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvcartaPrecio)
        val btnAgregar: Button = itemView.findViewById(R.id.agregar_producto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartaMenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cartamenu, parent, false)
        return CartaMenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartaMenuViewHolder, position: Int) {
        val producto = productos[position]
        holder.tvProducto.text = producto.producto
        holder.tvPrecio.text = "S/ ${producto.precio}"

        // Cargar imagen desde URL usando Glide
        Glide.with(holder.itemView.context)
            .load(producto.imagenUrl)
            .placeholder(R.drawable.ic_launcher_background) // Coloca una imagen temporal
            .error(R.drawable.ic_launcher_background)  // O cualquier otro recurso que ya exista
            // Imagen si hay error
            .into(holder.imagenProducto)

        holder.btnAgregar.setOnClickListener {
            // Puedes hacer algo como mostrar un Toast
            val context = holder.itemView.context
            Toast.makeText(context, "Agregado: ${producto.producto}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = productos.size
}
