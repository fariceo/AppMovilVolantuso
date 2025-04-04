package com.elrancho.cocina.ordenes

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*
import com.elrancho.cocina.R
import androidx.core.widget.addTextChangedListener


class PedidosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductoAdapter
    private lateinit var cantidadEditText: EditText
    private lateinit var totalTextView: TextView
    private lateinit var confirmarBtn: Button
    private lateinit var buscarEditText: EditText

    private var productoSeleccionado: Producto? = null
    private var usuario = "Byron" // puedes reemplazar con SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedidos)

        buscarEditText = findViewById(R.id.editTextBuscarProducto)
        recyclerView = findViewById(R.id.recyclerViewProductos)
        cantidadEditText = findViewById(R.id.editTextCantidad)
        totalTextView = findViewById(R.id.textViewTotal)
        confirmarBtn = findViewById(R.id.botonConfirmar)

        recyclerView.layoutManager = LinearLayoutManager(this)

        buscarEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length >= 2) {
                    buscarProducto(s.toString())
                }
            }
        })

        confirmarBtn.setOnClickListener {
            registrarPedido()
        }

        cantidadEditText.addTextChangedListener {
            actualizarTotal()
        }
    }

    private fun buscarProducto(nombre: String) {
        val url = "https://elpollovolantuso.com/asi_sistema/android/buscar_menu.php?nombre=$nombre"
        val request = StringRequest(com.android.volley.Request.Method.GET, url, { response ->
            val productos = JSONArray(response)
            val lista = mutableListOf<Producto>()
            for (i in 0 until productos.length()) {
                val item = productos.getJSONObject(i)
                val nombre = item.getString("producto")
                val precio = item.getDouble("precio")
                lista.add(Producto(nombre, precio))
            }
            adapter = ProductoAdapter(lista) { producto ->
                // Al hacer clic en un producto, actualizamos el EditText con el nombre
                productoSeleccionado = producto
                buscarEditText.setText(producto.nombre)  // Colocamos el nombre del producto en el EditText
                actualizarTotal()
            }
            recyclerView.adapter = adapter
        }, {
            Toast.makeText(this, "Error al buscar", Toast.LENGTH_SHORT).show()
        })

        Volley.newRequestQueue(this).add(request)
    }

    private fun actualizarTotal() {
        val cantidadStr = cantidadEditText.text.toString()
        val cantidad = cantidadStr.toIntOrNull() ?: 0
        val precio = productoSeleccionado?.precio ?: 0.0
        val total = cantidad * precio
        totalTextView.text = "Total: $%.2f".format(total)
    }

    private fun registrarPedido() {
        val cantidad = cantidadEditText.text.toString().toIntOrNull()
        val producto = productoSeleccionado ?: return

        if (cantidad == null || cantidad <= 0) {
            Toast.makeText(this, "Cantidad inválida", Toast.LENGTH_SHORT).show()
            return
        }

        val total = cantidad * producto.precio
        val fecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val hora = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

        val url = "https://elpollovolantuso.com/asi_sistema/android/insertar_producto.php"
        val request = object : StringRequest(com.android.volley.Request.Method.POST, url, {
            Toast.makeText(this, "Pedido registrado correctamente", Toast.LENGTH_SHORT).show()
        }, {
            Toast.makeText(this, "Error al registrar pedido", Toast.LENGTH_SHORT).show()
        }) {
            override fun getParams(): MutableMap<String, String> {
                return mutableMapOf(
                    "usuario" to usuario,
                    "producto" to producto.nombre,
                    "cantidad" to cantidad.toString(),
                    "precio" to producto.precio.toString(),
                    "total" to total.toString(),
                    "estado" to "0",
                    "delivery" to "default",
                    "metodo_pago" to "default",
                    "fecha" to fecha,
                    "hora" to hora
                )
            }
        }

        Volley.newRequestQueue(this).add(request)
    }
}
