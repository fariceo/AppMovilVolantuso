package com.elrancho.cocina.ordenes.pedidosclientes

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.elrancho.cocina.R
import com.elrancho.cocina.ordenes.pedidosclientes.PedidoClienteAdapter
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.toolbox.StringRequest
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class PedidoClienteActivity : AppCompatActivity() {

    private lateinit var recyclerPedidos: RecyclerView
    private lateinit var pedidoAdapter: PedidoClienteAdapter
    private lateinit var txtTotalGeneral: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedido_cliente)

        txtTotalGeneral = findViewById(R.id.txtTotalGeneral)

        recyclerPedidos = findViewById(R.id.recyclerPedidos)
        recyclerPedidos.layoutManager = LinearLayoutManager(this)

        cargarPedidos()
    }

    private fun cargarPedidos() {
        val url = "http://35.223.94.102/asi_sistema/android/pedidos_carrito.php"

        val request = StringRequest(Request.Method.GET, url,
            { response ->
                val jsonArray = JSONArray(response)
                val pedidos = mutableListOf<PedidoClienteModel>()
                var totalGeneral = 0.0

                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)
                    val pedido = PedidoClienteModel(
                        producto = item.getString("producto"),
                        cantidad = item.getInt("cantidad"),
                        precio = item.getDouble("precio"),
                        total = item.getDouble("total")
                    )
                    pedidos.add(pedido)
                    totalGeneral += pedido.total
                }

                pedidoAdapter = PedidoClienteAdapter(pedidos)
                recyclerPedidos.adapter = pedidoAdapter

                // ✅ Mostrar el total general formateado
                txtTotalGeneral.text = "Total: $%.2f".format(totalGeneral)
            },
            { error ->
                Toast.makeText(this, "Error al cargar pedidos", Toast.LENGTH_SHORT).show()
            })

        Volley.newRequestQueue(this).add(request)
    }
}
