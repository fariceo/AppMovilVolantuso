package com.elrancho.cocina.deudas

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.elrancho.cocina.R
import com.google.firebase.messaging.FirebaseMessaging

class PagosActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private val listaUsuarios = mutableListOf<UsuarioSaldo>()
    private lateinit var adapter: UsuarioSaldoAdapter

    private lateinit var tvTotalCobrar: TextView
    private lateinit var tvTotalPagar: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagos)

        // Suscripción a FCM
        FirebaseMessaging.getInstance().subscribeToTopic("todos")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //Log.d("FCM", "Suscripción a topic 'todos' exitosa")
                } else {
                    //Log.e("FCM", "Error al suscribirse al topic", task.exception)
                }
            }

        // Inicializar vistas
        tvTotalCobrar = findViewById(R.id.tvTotalCobrar)
        tvTotalPagar = findViewById(R.id.tvTotalPagar)
        recyclerView = findViewById(R.id.recyclerPagos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = UsuarioSaldoAdapter(listaUsuarios) {
            obtenerUsuarios() // callback cuando se actualiza un saldo
        }
        recyclerView.adapter = adapter

        obtenerUsuarios()
    }

    private fun obtenerUsuarios() {
        val url = "http://35.223.94.102/asi_sistema/android/buscar_saldo_pendiente.php"
        val requestQueue = Volley.newRequestQueue(this)

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    listaUsuarios.clear()
                    for (i in 0 until response.length()) {
                        val jsonObject = response.getJSONObject(i)
                        val usuario = jsonObject.getString("usuario")
                        val saldoPendiente = jsonObject.getDouble("saldo_pendiente")
                        val accion = jsonObject.getInt("accion")
                        listaUsuarios.add(UsuarioSaldo(usuario, saldoPendiente, accion))
                    }
                    adapter.notifyDataSetChanged()
                    calcularTotales()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error al procesar datos", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                error.printStackTrace()
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        requestQueue.add(jsonArrayRequest)
    }

    private fun calcularTotales() {
        var totalCobrar = 0.0
        var totalPagar = 0.0

        for (usuario in listaUsuarios) {
            when (usuario.accion) {
                1 -> totalCobrar += usuario.saldoPendiente
                2 -> totalPagar += usuario.saldoPendiente
            }
        }

        tvTotalCobrar.text = "Total a cobrar: $%.2f".format(totalCobrar)
        tvTotalPagar.text = "Total a pagar: $%.2f".format(totalPagar)
    }
}
