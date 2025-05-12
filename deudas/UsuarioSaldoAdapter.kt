package com.elrancho.cocina.deudas

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.elrancho.cocina.R
import com.elrancho.cocina.deudas.historialCredito.HistorialCreditoActivity

class UsuarioSaldoAdapter(
    private val usuarios: MutableList<UsuarioSaldo>,
    private val onSaldoActualizado: () -> Unit
) : RecyclerView.Adapter<UsuarioSaldoAdapter.UsuarioViewHolder>() {

    inner class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtUsuario: TextView = itemView.findViewById(R.id.txtUsuario)
        val txtSaldo: TextView = itemView.findViewById(R.id.txtSaldo)
        val txtAccion: TextView = itemView.findViewById(R.id.txtAccion)
        val btnEliminarDeuda: View = itemView.findViewById(R.id.btnEliminarDeuda)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_usuario_saldo, parent, false)
        return UsuarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuarioSaldo = usuarios[position]

        holder.txtUsuario.text = usuarioSaldo.usuario
        holder.txtSaldo.text = "Saldo: $%.2f".format(usuarioSaldo.saldoPendiente)
        holder.txtAccion.text = when (usuarioSaldo.accion) {
            1 -> "Cobrar"
            2 -> "Pagar"
            else -> "Acción"
        }

        // Colores por tipo de acción
        when (usuarioSaldo.accion) {
            1 -> holder.txtAccion.setTextColor(holder.itemView.context.getColor(R.color.verde))
            2 -> holder.txtAccion.setTextColor(holder.itemView.context.getColor(R.color.rojo))
            else -> holder.txtAccion.setTextColor(holder.itemView.context.getColor(R.color.black))
        }

        // Solo mostrar opciones cuando está en estado "Acción"
        holder.txtAccion.setOnClickListener {
            if (usuarioSaldo.accion == 0) {
                mostrarDialogoAccion(holder.itemView.context, usuarioSaldo)
            }
        }

        holder.txtSaldo.setOnClickListener {
            mostrarDialogoActualizarSaldo(holder.itemView.context, usuarioSaldo.usuario)
        }

        holder.btnEliminarDeuda.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("¿Eliminar deuda?")
                .setMessage("¿Estás seguro de eliminar la deuda de ${usuarioSaldo.usuario}? Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar") { _, _ ->
                    eliminarDeuda(holder.itemView.context, usuarioSaldo.usuario)
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        holder.txtUsuario.setOnClickListener {
            val context = holder.itemView.context
            val progressDialog = ProgressDialog(context).apply {
                setMessage("Cargando historial de crédito...")
                setCancelable(false)
                show()
            }

            val intent = Intent(context, HistorialCreditoActivity::class.java).apply {
                putExtra("usuario", usuarioSaldo.usuario)
            }

            Handler(Looper.getMainLooper()).postDelayed({
                progressDialog.dismiss()
                context.startActivity(intent)
            }, 1000)
        }
    }

    override fun getItemCount(): Int = usuarios.size

    private fun mostrarDialogoActualizarSaldo(context: Context, usuario: String) {
        val editText = EditText(context).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or
                    InputType.TYPE_NUMBER_FLAG_DECIMAL or
                    InputType.TYPE_NUMBER_FLAG_SIGNED
            hint = "Actualizar deuda de $usuario"
        }


        AlertDialog.Builder(context)
            .setTitle("Actualizar saldo de $usuario")
            .setView(editText)
            .setPositiveButton("Actualizar") { _, _ ->
                val nuevoSaldo = editText.text.toString().toDoubleOrNull()
                if (nuevoSaldo != null) {
                    actualizarSaldoEnServidor(context, usuario, nuevoSaldo)
                } else {
                    Toast.makeText(context, "Saldo inválido", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun actualizarSaldoEnServidor(context: Context, usuario: String, movimiento: Double) {
        val url = "http://35.223.94.102/asi_sistema/android/actualizar_saldo_pendiente.php"
        val requestQueue = Volley.newRequestQueue(context)

        val stringRequest = object : StringRequest(Request.Method.POST, url,
            { response ->
                Toast.makeText(context, response, Toast.LENGTH_SHORT).show()
                onSaldoActualizado()
                enviarNotificacionPushdePago(context, usuario, movimiento)
            },
            { error ->
                error.printStackTrace()
                Toast.makeText(context, "Error al actualizar: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                return mapOf("usuario" to usuario, "saldo_pendiente" to movimiento.toString())
            }
        }

        requestQueue.add(stringRequest)
    }

    private fun eliminarDeuda(context: Context, usuario: String) {
        val url = "http://35.223.94.102/asi_sistema/android/eliminar_deuda.php?usuario=$usuario"
        val requestQueue = Volley.newRequestQueue(context)

        val request = StringRequest(Request.Method.GET, url,
            { response ->
                Toast.makeText(context, response, Toast.LENGTH_SHORT).show()
                onSaldoActualizado()
                enviarNotificacionPush(context, usuario)
            },
            { error ->
                error.printStackTrace()
                Toast.makeText(context, "Error al eliminar deuda", Toast.LENGTH_SHORT).show()
            })

        requestQueue.add(request)
    }

    private fun enviarNotificacionPush(context: Context, usuario: String) {
        val url = "http://35.223.94.102/asi_sistema/android/notificacion_deuda_cancelada.php?usuario=$usuario"
        val requestQueue = Volley.newRequestQueue(context)

        val request = StringRequest(Request.Method.GET, url,
            {
                Toast.makeText(context, "Notificación enviada", Toast.LENGTH_SHORT).show()
            },
            { error ->
                error.printStackTrace()
                Toast.makeText(context, "Error al enviar notificación", Toast.LENGTH_SHORT).show()
            })

        requestQueue.add(request)
    }

    private fun enviarNotificacionPushdePago(context: Context, usuario: String, movimiento: Double) {
        val url = "http://35.223.94.102/asi_sistema/android/notificar_pago.php"
        val requestQueue = Volley.newRequestQueue(context)

        val request = object : StringRequest(Request.Method.POST, url,
            {}, { error -> error.printStackTrace() }) {
            override fun getParams(): Map<String, String> {
                return mapOf("usuario" to usuario, "monto" to movimiento.toString())
            }
        }

        requestQueue.add(request)
    }

    private fun mostrarDialogoAccion(context: Context, usuarioSaldo: UsuarioSaldo) {
        val opciones = arrayOf("Cobrar", "Pagar")
        AlertDialog.Builder(context)
            .setTitle("Selecciona una acción")
            .setItems(opciones) { _, which ->
                val nuevaAccion = if (which == 0) 1 else 2
                actualizarAccionEnServidor(context, usuarioSaldo.usuario, nuevaAccion)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun actualizarAccionEnServidor(context: Context, usuario: String, nuevaAccion: Int) {
        val url = "http://35.223.94.102/asi_sistema/android/actualizar_accion_pagos.php"
        val requestQueue = Volley.newRequestQueue(context)

        val request = object : StringRequest(Request.Method.POST, url,
            { Toast.makeText(context, "Acción actualizada", Toast.LENGTH_SHORT).show() },
            { error ->
                error.printStackTrace()
                Toast.makeText(context, "Error al actualizar acción", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                return mapOf("usuario" to usuario, "accion" to nuevaAccion.toString())
            }
        }

        requestQueue.add(request)
    }
}
