package com.elrancho.cocina.deudas

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
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
import android.text.InputType
import androidx.appcompat.app.AlertDialog


class PagosActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private val listaUsuarios = mutableListOf<UsuarioSaldo>()
    private lateinit var adapter: UsuarioSaldoAdapter

    private lateinit var tvTotalCobrar: TextView
    private lateinit var tvTotalPagar: TextView
    private lateinit var etBuscarUsuario: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagos)

        // Suscripción a FCM
        FirebaseMessaging.getInstance().subscribeToTopic("todos")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Subscribed successfully
                }
            }

        // Inicializar vistas
        tvTotalCobrar = findViewById(R.id.tvTotalCobrar)
        tvTotalPagar = findViewById(R.id.tvTotalPagar)
        etBuscarUsuario = findViewById(R.id.etBuscarUsuario)

        recyclerView = findViewById(R.id.recyclerPagos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = UsuarioSaldoAdapter(listaUsuarios) {
            buscarUsuario(etBuscarUsuario.text.toString()) // callback cuando se actualiza un saldo
        }
        recyclerView.adapter = adapter

        buscarUsuario("") // cargar todos al inicio

        etBuscarUsuario.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val texto = s.toString().trim()
                if (texto.isEmpty()) {
                    // Mostrar todos los usuarios nuevamente
                    buscarUsuario("")

                    // Quitar foco del EditText
                    etBuscarUsuario.clearFocus()

                    // Ocultar teclado
                    val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(etBuscarUsuario.windowToken, 0)
                } else {
                    // Buscar con filtro
                    buscarUsuario(texto)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        //agregar nuevo usuario
        val etNuevoUsuario: EditText = findViewById(R.id.etNuevoUsuario)
        val btnAgregarUsuario: android.widget.Button = findViewById(R.id.btnAgregarUsuario)

        btnAgregarUsuario.setOnClickListener {
            val nuevoUsuario = etNuevoUsuario.text.toString().trim()

            if (nuevoUsuario.isNotEmpty()) {
                registrarNuevoUsuario(nuevoUsuario)
            } else {
                Toast.makeText(this, "Ingresa un nombre de usuario", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun buscarUsuario(nombre: String) {
        val urlBase = "http://35.223.94.102/asi_sistema/android/buscar_saldo_pendiente.php"
        val url = if (nombre.isNotBlank()) "$urlBase?usuario=${nombre.trim()}" else urlBase

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




    ///agregar nuevo usuario en saldo_pendiente

    private fun registrarNuevoUsuario(nombre: String) {
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        input.hint = "Saldo pendiente (ej. 5.00)"

        AlertDialog.Builder(this)
            .setTitle("Nuevo usuario")
            .setMessage("Ingrese el saldo pendiente para $nombre")
            .setView(input)
            .setPositiveButton("Registrar") { _, _ ->
                val saldoTexto = input.text.toString()
                val saldo = if (saldoTexto.isNotEmpty()) saldoTexto else "0.0"
                enviarRegistroUsuario(nombre, saldo)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun enviarRegistroUsuario(nombre: String, saldo: String) {
        val url = "http://35.223.94.102/asi_sistema/android/insertar_usuario_saldo_pendiente.php"
        val requestQueue = Volley.newRequestQueue(this)

        val request = object : com.android.volley.toolbox.StringRequest(
            Method.POST, url,
            { response ->
                Toast.makeText(this, "Usuario agregado", Toast.LENGTH_SHORT).show()
                etBuscarUsuario.setText(nombre)
                buscarUsuario(nombre)
            },
            { error ->
                error.printStackTrace()
                Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                return mapOf("usuario" to nombre, "saldo" to saldo)
            }
        }

        requestQueue.add(request)
    }


}
