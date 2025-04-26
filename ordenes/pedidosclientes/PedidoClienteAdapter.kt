package com.elrancho.cocina.ordenes.pedidosclientes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.elrancho.cocina.R

data class PedidoCliente(val producto: String, val cantidad: Int, val total: Double)

class PedidoClienteAdapter(private val pedidos: List<PedidoClienteModel>) :
    RecyclerView.Adapter<PedidoClienteAdapter.PedidoViewHolder>() {

    class PedidoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productoText: TextView = view.findViewById(R.id.txtProducto)
        val cantidadText: TextView = view.findViewById(R.id.txtCantidad)
        val totalText: TextView = view.findViewById(R.id.txtTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pedido_cliente, parent, false)
        return PedidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = pedidos[position]
        holder.productoText.text = pedido.producto
        holder.cantidadText.text = "Cantidad: ${pedido.cantidad}"
        holder.totalText.text = "Total: $${pedido.total}"
    }

    override fun getItemCount(): Int = pedidos.size
}
