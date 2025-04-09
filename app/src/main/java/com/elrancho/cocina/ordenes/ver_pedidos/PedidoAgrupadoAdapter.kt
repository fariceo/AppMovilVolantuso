import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.elrancho.cocina.R
import com.elrancho.cocina.ordenes.ver_pedidos.PedidoAgrupado

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

        // Actualizar total con los costos de delivery
        actualizarTotal(pedido, holder)

        holder.layoutProductos.removeAllViews()

        var totalDelivery = 0.0 // Variable para calcular el costo total de delivery
        var checkBoxSelected = false // Variable para verificar si el CheckBox está seleccionado

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

            // Configurar RadioButtons según el tipo de delivery
            when (producto.delivery_type) {
                "takeout" -> {
                    radioTakeout.isChecked = true
                }
                "default" -> {
                    radioDefault.isChecked = true
                }
            }

            // Actualizar costo de delivery cuando se cambia la opción
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.radioTakeout -> {
                        producto.delivery_cost = 0.25 * producto.cantidad // Opción "para llevar"
                    }
                    R.id.radioDefault -> {
                        producto.delivery_cost = 0.0 // Opción "delivery"
                    }
                }

                // Actualizar el subtotal individual con delivery
                txtSubTotal.text = "Subtotal: $${"%.2f".format(producto.total + producto.delivery_cost)}"

                // Actualizar el total general del pedido
                actualizarTotal(pedido, holder)
            }

            holder.layoutProductos.addView(productoView)
        }

        // CheckBox para seleccionar delivery (solo aparece al final)
        val checkBoxDelivery = holder.itemView.findViewById<CheckBox>(R.id.checkBoxSelectDelivery)

        // Verificar si el CheckBox está seleccionado
        checkBoxDelivery.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkBoxSelected = true
            } else {
                checkBoxSelected = false
            }

            // Actualizar el total con el costo de delivery adicional
            actualizarTotal(pedido, holder, checkBoxSelected)
        }
    }

    override fun getItemCount(): Int = listaPedidos.size

    // Función para actualizar el total del pedido
    private fun actualizarTotal(pedido: PedidoAgrupado, holder: PedidoViewHolder, applyDelivery: Boolean = false) {
        var totalConDelivery = pedido.productos.sumOf { it.total + it.delivery_cost }

        // Si el CheckBox está seleccionado, añadir $2.5 al total
        if (applyDelivery) {
            totalConDelivery += 2.5
        }

        holder.txtTotal.text = "Total: $${"%.2f".format(totalConDelivery)}"
    }

    class PedidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtUsuario: TextView = itemView.findViewById(R.id.txtUsuario)
        val txtFecha: TextView = itemView.findViewById(R.id.txtFecha)
        val txtTotal: TextView = itemView.findViewById(R.id.txtTotal)
        val layoutProductos: ViewGroup = itemView.findViewById(R.id.layoutProductos)
    }
}
