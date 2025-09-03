package com.elrancho.cocina.ordenes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.elrancho.cocina.R

class PedidoAdapter(private val listaPedidos: List<Pedido>) :
    RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder>() {

    class PedidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtProducto: TextView = itemView.findViewById(R.id.txtProducto)
        val txtCantidad: TextView = itemView.findViewById(R.id.txtCantidad)
        val txtTotal: TextView = itemView.findViewById(R.id.txtTotal)
        val txtUsuario: TextView = itemView.findViewById(R.id.txtUsuario)
        val txtFecha: TextView = itemView.findViewById(R.id.txtFecha)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pedido, parent, false)
        return PedidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = listaPedidos[position]
        holder.txtProducto.text = "Producto: ${pedido.producto}"
        holder.txtCantidad.text = "Cantidad: ${pedido.cantidad}"
        holder.txtTotal.text = "Total: $${pedido.total}"
        holder.txtUsuario.text = "Usuario: ${pedido.usuario}"
        holder.txtFecha.text = "Fecha: ${pedido.fecha}"
    }

    override fun getItemCount(): Int = listaPedidos.size
}