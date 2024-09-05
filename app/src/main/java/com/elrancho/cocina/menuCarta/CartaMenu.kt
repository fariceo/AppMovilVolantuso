package com.elrancho.cocina.menuCarta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elrancho.cocina.R
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class CartaMenu : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val productoCartas = mutableListOf<ProductoCarta>()
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carta_menu)

        // Asegúrate de usar el ID correcto aquí
        recyclerView = findViewById(R.id.recyclerView2)  // Cambia a recyclerView2 si el ID en XML es recyclerView2
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = CartaMenuAdapter(productoCartas)

        // Añadir DividerItemDecoration al RecyclerView
        val dividerItemDecoration = DividerItemDecoration(
            this,
            (recyclerView.layoutManager as LinearLayoutManager).orientation
        )
        recyclerView.addItemDecoration(dividerItemDecoration)

        fetchProductos()
    }

    private fun fetchProductos() {
        val request = Request.Builder()
            .url("https://elpollovolantuso.com/asi_sistema/android/menu.php") // Cambia la URL por la tuya
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@CartaMenu, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val jsonArray = JSONArray(responseBody.string())
                    productoCartas.clear()  // Limpiar la lista antes de agregar nuevos elementos
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val producto: String = jsonObject.getString("producto")
                        val precio: Double = jsonObject.getDouble("precio")
                        productoCartas.add(ProductoCarta(producto, precio))
                    }

                    runOnUiThread {
                        recyclerView.adapter?.notifyDataSetChanged()
                    }
                }
            }
        })
    }
}
