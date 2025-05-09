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

class CartaMenuAdapter(
    private val productos: List<ProductoCarta>,
    private val listener: CartaMenuAdapterListener
) : RecyclerView.Adapter<CartaMenuAdapter.CartaMenuViewHolder>() {

    interface CartaMenuAdapterListener {
        /** Cuando el user pulsa “Agregar” */
        fun mostrarDialogoCantidad(producto: ProductoCarta)
    }

    inner class CartaMenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagenProducto: ImageView = itemView.findViewById(R.id.imagenProducto)
        val tvProducto: TextView    = itemView.findViewById(R.id.tvcartaProducto)
        val tvPrecio: TextView      = itemView.findViewById(R.id.tvcartaPrecio)
        val btnAgregar: Button      = itemView.findViewById(R.id.agregar_producto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartaMenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cartamenu, parent, false)
        return CartaMenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartaMenuViewHolder, position: Int) {
        val producto = productos[position]
        holder.tvProducto.text = producto.producto
        holder.tvPrecio.text   = "S/ ${producto.precio}"

        Glide.with(holder.itemView.context)
            .load(producto.imagenUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(holder.imagenProducto)

        holder.btnAgregar.setOnClickListener {
            // aquí solo delegamos a la Activity
            listener.mostrarDialogoCantidad(producto)
        }
    }

    override fun getItemCount(): Int = productos.size
}
