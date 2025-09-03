package com.elrancho.cocina.ordenes.ver_pedidos

import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.elrancho.cocina.R
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import android.widget.Toast
import android.content.Context

class PedidoAgrupadoAdapter(
    private val listaPedidos: MutableList<PedidoAgrupado>,
    private val listener: OnFiadoClickListener
) : RecyclerView.Adapter<PedidoAgrupadoAdapter.PedidoViewHolder>() {

    // Interfaz para manejar el click en el botón fiado
    interface OnFiadoClickListener {
        fun onAgregarFiado(saldo: Double, usuario: String, posicion: Int)
        fun onPedidoListo(usuario: String, total: Double, delivery_type: String, metodoPago: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pedido_agrupado, parent, false)
        return PedidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = listaPedidos[position]
        holder.txtUsuario.text = pedido.usuario
        holder.txtFecha.text = pedido.fecha

        holder.txtFecha.setOnClickListener {
            val context = holder.itemView.context
            val input = EditText(context)

            val hoy = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
            input.setText(hoy)
            input.inputType = InputType.TYPE_CLASS_DATETIME

            AlertDialog.Builder(context)
                .setTitle("Editar fecha")
                .setView(input)
                .setPositiveButton("Aceptar") { dialog, _ ->
                    val nuevaFecha = input.text.toString()
                    holder.txtFecha.text = nuevaFecha
                    pedido.fecha = nuevaFecha

                    val url = "http://35.223.94.102/asi_sistema/android/actualizar_fecha.php"
                    val request = object : StringRequest(
                        Method.POST, url,
                        { response ->
                            Toast.makeText(context, "Fecha actualizada", Toast.LENGTH_SHORT).show()
                        },
                        { error ->
                            Toast.makeText(context, "Error al actualizar", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        override fun getParams(): Map<String, String> {
                            val params = HashMap<String, String>()
                            params["id"] = pedido.id.toString()
                            params["fecha"] = nuevaFecha
                            return params
                        }
                    }

                    Volley.newRequestQueue(context).add(request)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        // Actualizar total con los costos de delivery
        actualizarTotal(pedido, holder)

        holder.layoutProductos.removeAllViews()

        var totalDelivery = 0.0
        var checkBoxSelected = false

        // Añadir productos
        for (producto in pedido.productos) {
            val productoView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.item_detalle_producto, holder.layoutProductos, false)

            val txtNombre = productoView.findViewById<TextView>(R.id.txtNombreProducto)
            val txtCantidad = productoView.findViewById<TextView>(R.id.txtCantidad)
            val txtPrecio = productoView.findViewById<TextView>(R.id.txtPrecio)
            val txtSubTotal = productoView.findViewById<TextView>(R.id.txtSubtotal)
            val radioGroup = productoView.findViewById<RadioGroup>(R.id.radioGroupDelivery)
            val radioTakeout = productoView.findViewById<RadioButton>(R.id.radioTakeout)
            val radioDefault = productoView.findViewById<RadioButton>(R.id.radioDefault)

            txtNombre.text = producto.producto
            txtCantidad.text = "Cantidad: ${producto.cantidad}"
            txtPrecio.text = "Precio: $${producto.precio}"
            txtSubTotal.text = "Subtotal: $${"%.2f".format(producto.total + producto.delivery_cost)}"

            when (producto.delivery_type) {
                "takeout" -> radioTakeout.isChecked = true
                "default" -> radioDefault.isChecked = true
            }

            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.radioTakeout -> {
                        producto.delivery_cost = 0.25 * producto.cantidad
                    }
                    R.id.radioDefault -> {
                        producto.delivery_cost = 0.0
                    }
                }

                txtSubTotal.text = "Subtotal: $${"%.2f".format(producto.total + producto.delivery_cost)}"
                actualizarTotal(pedido, holder)
            }

            holder.layoutProductos.addView(productoView)
        }

        val checkBoxDelivery = holder.itemView.findViewById<CheckBox>(R.id.checkBoxSelectDelivery)
        checkBoxDelivery.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkBoxSelected = true
                actualizarDeliveryEnBaseDeDatos(holder.itemView.context, pedido.usuario, "delivery")
            } else {
                checkBoxSelected = false
                actualizarDeliveryEnBaseDeDatos(holder.itemView.context, pedido.usuario, "no_delivery")
            }
            actualizarTotal(pedido, holder, checkBoxSelected)
        }

        // Agregar saldo fiado
        val buttonFiado = holder.itemView.findViewById<Button>(R.id.buttonFiado)
        buttonFiado.setOnClickListener {
            val context = holder.itemView.context
            val input = EditText(context)
            input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL

            val usuario = pedido.usuario
            val posicion = holder.adapterPosition

            AlertDialog.Builder(context)
                .setTitle("Agregar saldo fiado a $usuario")
                .setView(input)
                .setPositiveButton("Aceptar") { dialog, _ ->
                    val valorIngresado = input.text.toString().toDoubleOrNull()
                    if (valorIngresado != null) {
                        listener.onAgregarFiado(valorIngresado, usuario, posicion)
                    } else {
                        Toast.makeText(context, "Ingrese un número válido", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        // Marcar pedido como listo
        val btnPedidoListo = holder.itemView.findViewById<Button>(R.id.listo)
        btnPedidoListo.setOnClickListener {
            val totalText = holder.txtTotal.text.toString().replace("Total: $", "")
            val total = totalText.toDoubleOrNull() ?: 0.0

            val deliveryType = if (checkBoxSelected) "delivery" else "takeout"
            val metodoPago = pedido.metodo_pago // 👈 obtenemos el método de pago

            holder.itemView.animate()
                .alpha(0f)
                .translationX(holder.itemView.width.toFloat())
                .setDuration(500)
                .withEndAction {
                    listener.onPedidoListo(pedido.usuario, total, deliveryType, metodoPago)
                    eliminarPedidoPorUsuario(pedido.usuario)
                }
                .start()
        }
    }

    override fun getItemCount(): Int = listaPedidos.size

    private fun actualizarTotal(pedido: PedidoAgrupado, holder: PedidoViewHolder, applyDelivery: Boolean = false) {
        var totalConDelivery = pedido.productos.sumOf { it.total + it.delivery_cost }
        if (applyDelivery) totalConDelivery += 2.5
        holder.txtTotal.text = "Total: $${"%.2f".format(totalConDelivery)}"
    }

    fun eliminarPedidoPorUsuario(usuario: String) {
        val index = listaPedidos.indexOfFirst { it.usuario == usuario }
        if (index != -1) {
            listaPedidos.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    class PedidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtUsuario: TextView = itemView.findViewById(R.id.txtUsuario)
        val txtFecha: TextView = itemView.findViewById(R.id.txtFecha)
        val txtTotal: TextView = itemView.findViewById(R.id.txtTotal)
        val layoutProductos: ViewGroup = itemView.findViewById(R.id.layoutProductos)
    }

    private fun actualizarDeliveryEnBaseDeDatos(context: Context, usuario: String, tipoEntrega: String) {
        val url = "http://35.223.94.102/asi_sistema/android/actualizar_delivery.php"

        val stringRequest = object : StringRequest(Request.Method.POST, url,
            Response.Listener { response ->
                Toast.makeText(context, "Pedido actualizado correctamente", Toast.LENGTH_SHORT).show()
            },
            Response.ErrorListener { error ->
                Toast.makeText(context, "Error en la conexión: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {

            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["usuario"] = usuario  // Aquí pasas el usuario que ya tienes en el pedido
                params["delivery"] = tipoEntrega
                return params
            }
        }

        Volley.newRequestQueue(context).add(stringRequest)
    }
}
