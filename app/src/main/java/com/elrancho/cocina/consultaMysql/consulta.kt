package com.elrancho.cocina.consultaMysql

import com.elrancho.cocina.R
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

import androidx.recyclerview.widget.DividerItemDecoration


class consulta : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val productos = mutableListOf<Producto>()
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consulta)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ProductoAdapter(productos)

// Añadir DividerItemDecoration al RecyclerView, lo que es lo mismo es dividir cada item del recicler view
        val dividerItemDecoration = DividerItemDecoration(this, (recyclerView.layoutManager as LinearLayoutManager).orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)



        fetchProductos()

    }

    private fun fetchProductos() {
        val request = Request.Builder()
            .url("https://elpollovolantuso.com/testing.php")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@consulta, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val jsonArray = JSONArray(responseBody.string())
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val usuario = jsonObject.getString("usuario")
                        val producto = jsonObject.getString("producto")
                        productos.add(Producto(usuario, producto))
                    }

                    runOnUiThread {
                        recyclerView.adapter?.notifyDataSetChanged()
                    }
                }
            }
        })
    }
}
