package com.elrancho.cocina.productos.puntoventa

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.elrancho.cocina.R
import com.elrancho.cocina.scanner.ScannerActivity
import org.json.JSONObject

class PuntoVentaActivity : AppCompatActivity() {

    private val carrito = mutableListOf<ProductoCarrito>()
    private lateinit var adapter: CarritoAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var mediaPlayer: MediaPlayer
    private val negocio = "nombre_negocio_demo" // Cambiar dinámicamente según tu lógica
    private val usuario = "usuario_demo"       // Cambiar dinámicamente según sesión

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_punto_venta)

        recyclerView = findViewById(R.id.recyclerCarrito)
        adapter = CarritoAdapter(carrito, ::onItemChanged, ::onItemRemoved)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        mediaPlayer = MediaPlayer.create(this, R.raw.beep)

        val btnScan = findViewById<Button>(R.id.btnScan)
        btnScan.setOnClickListener {
            val intent = Intent(this, ScannerActivity::class.java)
            startActivityForResult(intent, 1001)
        }

        cargarCarritoRemoto()
    }

    private fun cargarCarritoRemoto() {
        val url = "https://elpollovolantuso.com/negocioencontrol/api/obtener_carrito.php"
        val request = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                try {
                    val json = JSONObject(response)
                    if (json.optBoolean("success", false)) {
                        val array = json.optJSONArray("carrito")
                        carrito.clear()
                        for (i in 0 until array!!.length()) {
                            val item = array.getJSONObject(i)
                            carrito.add(
                                ProductoCarrito(
                                    item.optInt("id_producto"),
                                    item.optString("producto"),
                                    item.optInt("cantidad"),
                                    item.optDouble("precio")
                                )
                            )
                        }
                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this, "No hay productos en el carrito", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error procesando carrito", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                error.printStackTrace()
                Toast.makeText(this, "Error conexión al servidor", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> =
                hashMapOf("negocio" to negocio, "usuario" to usuario)
        }
        Volley.newRequestQueue(this).add(request)
    }

    private fun onItemChanged(producto: ProductoCarrito) {
        actualizarCarritoRemoto(producto)
    }

    private fun onItemRemoved(producto: ProductoCarrito) {
        eliminarCarritoRemoto(producto)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            val codigo = data?.getStringExtra("codigo")
            if (!codigo.isNullOrEmpty()) {
                buscarProductoPorCodigo(codigo)
            }
        }
    }

    private fun buscarProductoPorCodigo(codigo: String) {
        val url = "https://elpollovolantuso.com/negocioencontrol/api/buscar_producto_codigo.php"

        val request = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                try {
                    val json = JSONObject(response)
                    if (json.optBoolean("success", false)) {
                        val p = json.optJSONObject("producto")
                        if (p != null) {
                            val producto = ProductoCarrito(
                                p.optInt("id_producto"),
                                p.optString("producto"),
                                1,
                                p.optDouble("precio")
                            )
                            agregarCarritoRemoto(producto)
                        }
                    } else {
                        Toast.makeText(this, json.optString("msg", "Producto no encontrado"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error procesando producto", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                error.printStackTrace()
                Toast.makeText(this, "Error conexión al servidor", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> =
                hashMapOf("codigo" to codigo)
        }
        Volley.newRequestQueue(this).add(request)
    }

    private fun agregarCarritoRemoto(producto: ProductoCarrito) {
        val url = "https://elpollovolantuso.com/negocioencontrol/api/agregar_carrito.php"
        val request = object : StringRequest(Request.Method.POST, url,
            { response ->
                try {
                    val json = JSONObject(response)
                    if (json.optBoolean("success", false)) {
                        mediaPlayer.start()
                        cargarCarritoRemoto()
                    } else {
                        Toast.makeText(this, json.optString("msg"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error agregando producto", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                error.printStackTrace()
                Toast.makeText(this, "Error conexión al servidor", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> = hashMapOf(
                "negocio" to negocio,
                "usuario" to usuario,
                "id_producto" to producto.id.toString(),
                "cantidad" to producto.cantidad.toString()
            )
        }
        Volley.newRequestQueue(this).add(request)
    }

    private fun actualizarCarritoRemoto(producto: ProductoCarrito) {
        val url = "https://elpollovolantuso.com/negocioencontrol/api/actualizar_carrito.php"
        val request = object : StringRequest(Request.Method.POST, url,
            { response -> cargarCarritoRemoto() },
            { error -> error.printStackTrace() }
        ) {
            override fun getParams(): MutableMap<String, String> = hashMapOf(
                "negocio" to negocio,
                "usuario" to usuario,
                "id_producto" to producto.id.toString(),
                "cantidad" to producto.cantidad.toString()
            )
        }
        Volley.newRequestQueue(this).add(request)
    }

    private fun eliminarCarritoRemoto(producto: ProductoCarrito) {
        val url = "https://elpollovolantuso.com/negocioencontrol/api/eliminar_carrito.php"
        val request = object : StringRequest(Request.Method.POST, url,
            { response -> cargarCarritoRemoto() },
            { error -> error.printStackTrace() }
        ) {
            override fun getParams(): MutableMap<String, String> = hashMapOf(
                "negocio" to negocio,
                "usuario" to usuario,
                "id_producto" to producto.id.toString()
            )
        }
        Volley.newRequestQueue(this).add(request)
    }
}
