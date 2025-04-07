package com.elrancho.cocina.ordenes

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.elrancho.cocina.R
import org.json.JSONObject

import com.elrancho.cocina.ordenes.PedidosActivity



/*Archivos envolucrados para el funionamiento de "ListapedidosActivity"
* PedidosActivity.kt
* PedidoAdapter.kt
* Pedido.kt
* activity_listapedidos.xml
* item_pedido.xml
* https://elpollovolantuso.com/asi_sistema/android/pedidos_android.php
* */

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


// Botón (logo) para volver a ingresar un nuevo pedido
        val btnVolverIngresarPedido: ImageView = findViewById(R.id.logoImageView)

        btnVolverIngresarPedido.setOnClickListener {
            // Crear un Intent para iniciar la actividad PedidosActivity
            val intent = Intent(this@ListapedidosActivity, PedidosActivity::class.java)
            startActivity(intent)
        }



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