package com.elrancho.cocina.compras

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import org.json.JSONException
import android.util.Log

import com.elrancho.cocina.R

class GastosAdapter(private val listaGastos: MutableList<Gasto>) :
    RecyclerView.Adapter<GastosAdapter.GastoViewHolder>() {
    var onEditarPrecio: ((Gasto) -> Unit)? = null
    class GastoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtProducto: TextView = view.findViewById(R.id.txtProducto)
        val txtPrecio: TextView = view.findViewById(R.id.txtPrecio)
        val checkBox: CheckBox = view.findViewById(R.id.checkBoxGasto) // Referencia correcta al CheckBox
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GastoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gasto, parent, false)
        return GastoViewHolder(view)
    }

    override fun onBindViewHolder(holder: GastoViewHolder, position: Int) {
        val gasto = listaGastos[position]
        holder.txtProducto.text = gasto.producto
        holder.txtPrecio.text = "S/. ${gasto.precio}"
        // Si el gasto tiene estado 1, cambiar la opacidad al 75%
        if (gasto.estado == 1) {
            holder.itemView.alpha = 0.75f  // Reducir opacidad
            holder.checkBox.isEnabled = false  // Deshabilitar interacción
        } else {
            holder.itemView.alpha = 1.0f  // Normal
            holder.checkBox.isEnabled = true  // Habilitar interacción
        }
        // Eliminar el listener anterior antes de establecer el nuevo estado
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = gasto.estado == 1

        // Asignar nuevo listener
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (gasto.estado == 1) {
                // Si el estado ya es 1, mostrar alerta
                Toast.makeText(holder.itemView.context, "Este producto ya ha sido adquirido", Toast.LENGTH_SHORT).show()
            } else {
                val nuevoEstado = if (isChecked) 1 else 0
                actualizarEstadoGasto(holder.itemView.context, gasto.id, nuevoEstado)
            }
        }

        holder.txtPrecio.setOnClickListener {
            if (gasto.estado == 0) {
                onEditarPrecio?.invoke(gasto)
            } else {
                Toast.makeText(holder.itemView.context, "Este producto ya fue adquirido", Toast.LENGTH_SHORT).show()
            }
        }

    }


    override fun getItemCount(): Int = listaGastos.size

    // Método para ordenar la lista de gastos antes de pasarla al RecyclerView
    fun sortGastos() {
        // Se ordenan primero los elementos con estado 0
        listaGastos.sortedWith(compareBy { it.estado })
    }
    /*fun actualizarLista(nuevaLista: List<Gasto>) {
        listaGastos = nuevaLista.toMutableList()
        notifyDataSetChanged()
    }

    fun sortGastos() {
        listaGastos.sortBy { it.estado }
        notifyDataSetChanged()
    }
*/
    fun actualizarEstadoGasto(context: Context, id: Int, estado: Int) {
        Log.d("ActualizarEstado", "Enviando ID: $id, Estado: $estado") // Verificar datos antes de enviar

        val url = "https://elpollovolantuso.com/asi_sistema/android/actualizar_estado_gasto.php"
        val requestQueue = Volley.newRequestQueue(context)

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                Log.d("ActualizarEstado", "Respuesta del servidor: $response") // Verificar respuesta del servidor

                try {
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getBoolean("success")

                    if (!success) {
                        Toast.makeText(context, "Error al actualizar estado", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    Log.e("ActualizarEstado", "Error JSON: ${e.message}")
                    Toast.makeText(context, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("ActualizarEstado", "Error de conexión: ${error.message}")
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = hashMapOf(
                    "id" to id.toString(),
                    "estado" to estado.toString()
                )
                Log.d("ActualizarEstado", "Parámetros enviados: $params") // Verificar datos enviados
                return params
            }
        }

        requestQueue.add(stringRequest)
    }
}
