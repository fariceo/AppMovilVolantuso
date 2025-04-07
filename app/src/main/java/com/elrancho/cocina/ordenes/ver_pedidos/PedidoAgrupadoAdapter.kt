package com.elrancho.cocina.ordenes.ver_pedidos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.elrancho.cocina.R

class PedidoAgrupadoAdapter(private val listaPedidos: List<PedidoAgrupado>) :
    RecyclerView.Adapter<PedidoAgrupadoAdapter.PedidoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pedido_agrupado, parent, false)
        return PedidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = listaPedidos[position]
        holder.txtUsuario.text = pedido.usuario
        holder.txtFecha.text = pedido.fecha
        holder.txtTotal.text = "Total: $${pedido.total}"

        // Limpiar productos anteriores
        holder.layoutProductos.removeAllViews()

        // Agregar productos individualmente
        for (producto in pedido.productos) {
            val productoView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.item_detalle_producto, holder.layoutProductos, false)

            val txtNombre = productoView.findViewById<TextView>(R.id.txtNombreProducto)
            val txtCantidad = productoView.findViewById<TextView>(R.id.txtCantidad)
            val txtPrecio = productoView.findViewById<TextView>(R.id.txtPrecio)
            val txtSubTotal = productoView.findViewById<TextView>(R.id.txtSubtotal)

            txtNombre.text = producto.producto
            txtCantidad.text = "Cantidad: ${producto.cantidad}"
            txtPrecio.text = "Precio: $${producto.precio}"
            txtSubTotal.text = "Subtotal: $${producto.total}"

            holder.layoutProductos.addView(productoView)
        }
    }

    override fun getItemCount(): Int = listaPedidos.size

    class PedidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtUsuario: TextView = itemView.findViewById(R.id.txtUsuario)
        val txtFecha: TextView = itemView.findViewById(R.id.txtFecha)
        val txtTotal: TextView = itemView.findViewById(R.id.txtTotal)
        val layoutProductos: LinearLayout = itemView.findViewById(R.id.layoutProductos)
    }
}
