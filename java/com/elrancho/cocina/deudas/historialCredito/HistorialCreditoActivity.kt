package com.elrancho.cocina.deudas.historialCredito

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.elrancho.cocina.R
import org.json.JSONArray
import android.widget.TextView

class HistorialCreditoActivity : AppCompatActivity() {

    private lateinit var recyclerHistorial: RecyclerView
    private lateinit var historialAdapter: HistorialCreditoAdapter
    private val listaHistorial = mutableListOf<HistorialCredito>()
    private var usuario: String = "" // Cambié a var para permitir la reasignación

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial_credito)

        // Recibimos el usuario que fue enviado desde la otra actividad
        usuario = intent.getStringExtra("usuario") ?: ""  // Asignamos un valor por defecto si es nulo

        // Mostramos el usuario en el TextView
        val txtNombreUsuario = findViewById<TextView>(R.id.txtNombreUsuario)
        txtNombreUsuario.text = "Historial de crédito de: $usuario"

        recyclerHistorial = findViewById(R.id.recyclerHistorialCredito)
        recyclerHistorial.layoutManager = LinearLayoutManager(this)

        historialAdapter = HistorialCreditoAdapter(listaHistorial)
        recyclerHistorial.adapter = historialAdapter

        obtenerHistorialDesdeServidor()
    }
    private fun obtenerHistorialDesdeServidor() {
        val url = "http://35.223.94.102/asi_sistema/android/historial_credito_usuario.php?usuario=$usuario"

        val request = StringRequest(Request.Method.GET, url,
            { response ->
                try {
                    val jsonArray = JSONArray(response)
                    listaHistorial.clear()  // Limpiamos la lista antes de agregar nuevos elementos
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        val fecha = obj.getString("fecha")
                        val saldo = obj.getDouble("saldo")
                        val saldoContable = obj.getDouble("saldo_contable")
                        val usuario = obj.getString("usuario")  // Asumí que el JSON también tiene un campo "usuario"

                        // Agregar la información a la lista
                        listaHistorial.add(HistorialCredito(fecha, saldo, saldoContable, usuario))
                    }
                    // Notificar al adaptador para que actualice el RecyclerView
                    historialAdapter.notifyDataSetChanged()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error al parsear datos", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                error.printStackTrace()
                Toast.makeText(this, "Error de conexión: ${error.message}", Toast.LENGTH_SHORT).show()
            })

        // Enviar la solicitud a la cola de Volley
        Volley.newRequestQueue(this).add(request)
    }

}
