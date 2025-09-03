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
        holder.tvCantidad.text = producto.cantidad.toString()  // Convertir la cantidad a String
        holder.tvPrecio.text = "Precio: $${String.format("%.2f", producto.precio)}"
        holder.tvPrecio.text = "Total: $${String.format("%.2f", producto.total)}"
        holder.tvFecha.text = producto.fecha.toString()       // Convertir la fecha a String
    }

    override fun getItemCount() = productos.size

    class ProductoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUsuario: TextView = view.findViewById(R.id.tvUsuario)
        val tvProducto: TextView = view.findViewById(R.id.tvProducto)
        val tvCantidad: TextView = view.findViewById(R.id.tvCantidad)
        val tvPrecio: TextView = view.findViewById(R.id.tvPrecio)  // Asegúrate de que este TextView esté en el XML
        val tvTotal: TextView = view.findViewById(R.id.tvTotal)    // Asegúrate de que este TextView esté en el XML
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)    // Asegúrate de que este TextView esté en el XML
    }
}
