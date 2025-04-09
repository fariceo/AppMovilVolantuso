package com.elrancho.cocina.ordenes.ver_pedidos

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.elrancho.cocina.R

class VerPedidosActivity : AppCompatActivity(), PedidoAgrupadoAdapter.OnFiadoClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PedidoAgrupadoAdapter
    private val listaAgrupada = mutableListOf<PedidoAgrupado>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_pedidos)

        recyclerView = findViewById(R.id.recyclerPedidosAgrupados)
        recyclerView.layoutManager = LinearLayoutManager(this)

        obtenerPedidosAgrupados()
    }

    private fun obtenerPedidosAgrupados() {
        val url = "https://elpollovolantuso.com/asi_sistema/android/pedidos_agrupados.php"
        val request = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                listaAgrupada.clear()

                for (i in 0 until response.length()) {
                    val obj = response.getJSONObject(i)
                    val productosArray = obj.getJSONArray("productos")

                    val listaProductos = mutableListOf<DetalleProducto>()
                    for (j in 0 until productosArray.length()) {
                        val p = productosArray.getJSONObject(j)
                        val producto = DetalleProducto(
                            producto = p.getString("producto"),
                            cantidad = p.getInt("cantidad"),
                            precio = p.getDouble("precio"),
                            total = p.getDouble("total"),
                            fecha = p.getString("fecha"),
                            delivery_type = p.getString("delivery"),
                            delivery_cost = p.getDouble("delivery_cost")
                        )
                        listaProductos.add(producto)
                    }

                    val pedido = PedidoAgrupado(
                        usuario = obj.getString("usuario"),
                        total = obj.getDouble("total"),
                        fecha = obj.getString("fecha"),
                        productos = listaProductos
                    )
                    listaAgrupada.add(pedido)
                }

                adapter = PedidoAgrupadoAdapter(listaAgrupada, this) // Pasa el listener
                recyclerView.adapter = adapter
            },
            { error -> error.printStackTrace() })

        Volley.newRequestQueue(this).add(request)
    }

    // Implementación del método de la interfaz
    override fun onAgregarFiado(saldo: Double, usuario: String) {
        actualizarSaldoFiadoEnServidor(saldo, usuario)
    }

    private fun actualizarSaldoFiadoEnServidor(saldo: Double, usuario: String) {
        val url = "https://elpollovolantuso.com/asi_sistema/android/actualizar_saldo.php"

        val request = object : com.android.volley.toolbox.StringRequest(Method.POST, url,
            { response ->
                Toast.makeText(this, "Saldo fiado agregado correctamente", Toast.LENGTH_SHORT).show()
            },
            { error ->
                Toast.makeText(this, "Error al agregar saldo: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                return mapOf(
                    "usuario" to usuario,
                    "saldo" to saldo.toString()
                )
            }
        }

        Volley.newRequestQueue(this).add(request)
    }
}
