package com.elrancho.cocina.ordenes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.elrancho.cocina.R
import org.json.JSONObject

class ListapedidosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PedidoAdapter
    private val listaPedidos = mutableListOf<Pedido>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listapedidos)

        recyclerView = findViewById(R.id.recyclerPedidos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PedidoAdapter(listaPedidos)
        recyclerView.adapter = adapter

        obtenerPedidos()
    }

    private fun obtenerPedidos() {
        val url = "https://elpollovolantuso.com/asi_sistema/android/pedidos_android.php"

        val request = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                for (i in 0 until response.length()) {
                    val jsonObj: JSONObject = response.getJSONObject(i)
                    val pedido = Pedido(

                        producto = jsonObj.getString("producto"),
                        cantidad = jsonObj.getInt("cantidad"),
                        total = jsonObj.getDouble("total"),
                        usuario = jsonObj.getString("usuario"),
                        fecha = jsonObj.getString("fecha")
                        )
                    listaPedidos.add(pedido)
                }
                adapter.notifyDataSetChanged()
                // Dentro de ListapedidosActivity.kt
                adapter = PedidoAdapter(listaPedidos)
                recyclerView.adapter = adapter

            },
            { error ->
                error.printStackTrace()
            })

        Volley.newRequestQueue(this).add(request)
    }
}
