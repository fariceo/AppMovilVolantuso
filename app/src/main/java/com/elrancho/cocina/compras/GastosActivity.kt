package com.elrancho.cocina.compras

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.elrancho.cocina.R
import org.json.JSONException
import org.json.JSONObject

class GastosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var gastosAdapter: GastosAdapter
    private lateinit var formulario: LinearLayout
    private lateinit var inputProducto: EditText
    private lateinit var inputCantidad: EditText
    private lateinit var inputPrecio: EditText
    private lateinit var btnRegistrar: Button
    private val listaGastos = mutableListOf<Gasto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gastos)

        recyclerView = findViewById(R.id.recyclerGastos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        formulario = findViewById(R.id.formularioRegistro)
        inputProducto = findViewById(R.id.inputProducto)
        inputCantidad = findViewById(R.id.inputCantidad)
        inputPrecio = findViewById(R.id.inputPrecio)
        btnRegistrar = findViewById(R.id.btnRegistrarCompra)

        val botonDiario: Button = findViewById(R.id.botonDiario)
        val botonSemanal: Button = findViewById(R.id.botonSemanal)
        val botonMensual: Button = findViewById(R.id.botonMensual)

        botonDiario.setOnClickListener {
            obtenerGastos("diario")
            formulario.visibility = View.VISIBLE
        }
        botonSemanal.setOnClickListener {
            obtenerGastos("semanal")
            formulario.visibility = View.GONE
        }
        botonMensual.setOnClickListener {
            obtenerGastos("mensual")
            formulario.visibility = View.GONE
        }

        btnRegistrar.setOnClickListener { registrarCompra() }
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
                        val id = item.getInt("id") // Obtiene el ID desde la API
                        val producto = item.getString("producto")
                        val precio = item.getDouble("total")
                        val estado = item.getInt("estado")

                        listaGastos.add(Gasto(id, producto, precio, estado)) // Ahora se agrega con el ID correcto
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

    private fun registrarCompra() {
        val producto = inputProducto.text.toString().trim()
        val cantidad = inputCantidad.text.toString().trim()
        val precio = inputPrecio.text.toString().trim()

        if (producto.isEmpty() || cantidad.isEmpty() || precio.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "https://elpollovolantuso.com/asi_sistema/android/registro_gasto.php"
        val requestQueue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getBoolean("success")
                    if (success) {
                        Toast.makeText(this, "Compra registrada con éxito", Toast.LENGTH_SHORT).show()
                        inputProducto.text.clear()
                        inputCantidad.text.clear()
                        inputPrecio.text.clear()
                        obtenerGastos("diario")
                    } else {
                        Toast.makeText(this, "Error al registrar compra", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(this, "Error en el servidor", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error en la conexión: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["producto"] = producto
                params["cantidad"] = cantidad
                params["precio"] = precio
                return params
            }
        }

        requestQueue.add(stringRequest)
    }
}
