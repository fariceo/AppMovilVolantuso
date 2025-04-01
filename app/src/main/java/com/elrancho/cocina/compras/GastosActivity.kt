package com.elrancho.cocina.compras

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.elrancho.cocina.R
import org.json.JSONException

class GastosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var gastosAdapter: GastosAdapter
    private val listaGastos = mutableListOf<Gasto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gastos)

        recyclerView = findViewById(R.id.recyclerGastos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Botones
        val botonDiario: Button = findViewById(R.id.botonDiario)
        val botonSemanal: Button = findViewById(R.id.botonSemanal)
        val botonMensual: Button = findViewById(R.id.botonMensual)

        // Eventos de botones para filtrar gastos
        botonDiario.setOnClickListener { obtenerGastos("diario") }
        botonSemanal.setOnClickListener { obtenerGastos("semanal") }
        botonMensual.setOnClickListener { obtenerGastos("mensual") }

        // Carga inicial (puede ser el diario por defecto)
        obtenerGastos("diario")
    }

    private fun obtenerGastos(filtro: String) {
        val url = "https://elpollovolantuso.com/asi_sistema/android/consulta_gastos.php?filtro=$filtro"
        val requestQueue = Volley.newRequestQueue(this)

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    listaGastos.clear()
                    for (i in 0 until response.length()) {
                        val item = response.getJSONObject(i)
                        val producto = item.getString("producto")
                        val precio = item.getDouble("total")
                        listaGastos.add(Gasto(producto, precio))
                    }
                    gastosAdapter = GastosAdapter(listaGastos)
                    recyclerView.adapter = gastosAdapter
                } catch (e: JSONException) {
                    Toast.makeText(this, "Error al procesar datos", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error en la conexión: ${error.message}", Toast.LENGTH_SHORT).show()
            })

        requestQueue.add(jsonArrayRequest)
    }
}
