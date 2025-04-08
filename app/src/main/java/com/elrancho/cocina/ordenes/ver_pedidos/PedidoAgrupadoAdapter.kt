package com.elrancho.cocina.ordenes.ver_pedidos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
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

        // Limpiar los productos previos antes de agregar nuevos
        holder.layoutProductos.removeAllViews()

        for (producto in pedido.productos) {
            // Inflar el layout para un solo producto
            val productoView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.item_detalle_producto, holder.layoutProductos, false)

            // Obtener las vistas dentro de cada producto
            val txtNombre = productoView.findViewById<TextView>(R.id.txtNombreProducto)
            val txtCantidad = productoView.findViewById<TextView>(R.id.txtCantidad)
            val txtPrecio = productoView.findViewById<TextView>(R.id.txtPrecio)
            val txtSubTotal = productoView.findViewById<TextView>(R.id.txtSubtotal)
            val radioGroup = productoView.findViewById<RadioGroup>(R.id.radioGroupDelivery)
            val radioDelivery = productoView.findViewById<RadioButton>(R.id.radioDelivery)
            val radioTakeout = productoView.findViewById<RadioButton>(R.id.radioTakeout)
            val radioDefault = productoView.findViewById<RadioButton>(R.id.radioDefault)

            // Asignar los datos del producto
            txtNombre.text = producto.producto
            txtCantidad.text = "Cantidad: ${producto.cantidad}"
            txtPrecio.text = "Precio: $${producto.precio}"
            txtSubTotal.text = "Subtotal: $${producto.total}"

            // Configurar los radio buttons según el tipo de delivery
            when (producto.delivery_type) {
                "delivery" -> {
                    radioDelivery.isChecked = true
                }
                "takeout" -> {
                    radioTakeout.isChecked = true
                }
                "default" -> {
                    radioDefault.isChecked = true
                }
            }

            // Cambiar el costo de delivery cuando el usuario seleccione una opción
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.radioDelivery -> {
                        producto.delivery_cost = 2.5
                    }
                    R.id.radioTakeout -> {
                        producto.delivery_cost = 0.25 * producto.cantidad
                    }
                    R.id.radioDefault -> {
                        producto.delivery_cost = 0.0
                    }
                }

                // Actualizar el total después de cambiar el costo de delivery
                val nuevoTotal = producto.total + producto.delivery_cost
                txtSubTotal.text = "Subtotal: $${nuevoTotal}"

                // También actualizar el total del pedido en el ViewHolder
                holder.txtTotal.text = "Total: $${nuevoTotal}"
            }

            // Agregar la vista inflada del producto al layoutProductos
            holder.layoutProductos.addView(productoView)
        }
    }

    override fun getItemCount(): Int = listaPedidos.size

    class PedidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtUsuario: TextView = itemView.findViewById(R.id.txtUsuario)
        val txtFecha: TextView = itemView.findViewById(R.id.txtFecha)
        val txtTotal: TextView = itemView.findViewById(R.id.txtTotal)
        val layoutProductos: ViewGroup = itemView.findViewById(R.id.layoutProductos)
    }
}
