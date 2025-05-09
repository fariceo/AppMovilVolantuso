package com.elrancho.cocina.menuCarta

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.elrancho.cocina.R

import com.elrancho.cocina.menuCarta.CartaMenuAdapter
import com.elrancho.cocina.menuCarta.CartaMenuAdapter.CartaMenuAdapterListener
import com.elrancho.cocina.ordenes.pedidosclientes.PedidoClienteActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request as OkHttpRequest
import okhttp3.Response as OkHttpResponse
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import android.text.InputType

class CartaMenu : AppCompatActivity(), CartaMenuAdapterListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var recyclerView: RecyclerView
    private val productoCartas = mutableListOf<ProductoCarta>()
    private val client = OkHttpClient()
    private lateinit var nombreUsuario: String
    private lateinit var btnConfirmarPedido: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carta_menu)



        // Usuario logueado
        nombreUsuario = intent.getStringExtra("usuario") ?: "test"
        actualizarContadorCarrito(nombreUsuario)

        // Carrito
        val imgCarrito = findViewById<ImageView>(R.id.imgCarrito)
        val cartItemCount = findViewById<TextView>(R.id.cartItemCount)

        cartItemCount.text = "0"
        // Cargar la imagen del carrito desde la URL usando Glide
        val carritoImageUrl = "http://35.223.94.102/imagenes/carrito.png"
        Glide.with(this)
            .load(carritoImageUrl)
            .into(imgCarrito)  // Asigna la imagen al ImageView

        // Acción al hacer clic en el carrito
        imgCarrito.setOnClickListener {
            startActivity(Intent(this, PedidoClienteActivity::class.java))
        }


        // Confirmar pedidos
        findViewById<Button>(R.id.btnConfirmarPedido).setOnClickListener { confirmarPedidos() }

        btnConfirmarPedido = findViewById(R.id.btnConfirmarPedido)
        // Inicialmente ocultamos el botón
        btnConfirmarPedido.visibility = View.GONE


        // Toolbar y Drawer
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigationView)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.open_drawer, R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        cargarCategoriasEnMenu()
        navigationView.setNavigationItemSelectedListener { menuItem ->
            handleNavigation(menuItem)
            drawerLayout.closeDrawers()
            true
        }

        // RecyclerView
        recyclerView = findViewById(R.id.recyclerView2)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(
            DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        )
        recyclerView.adapter = CartaMenuAdapter(productoCartas, this)

        // Carga inicial de productos y visibilidad del botón
        fetchProductos()
        verificarMostrarBoton()
    }

    private fun fetchProductos(categoria: String? = null) {
        val url = if (categoria == null) {
            "http://35.223.94.102/asi_sistema/android/menu.php"
        } else {
            "http://35.223.94.102/asi_sistema/android/menu.php?categoria=$categoria"
        }

        val request = OkHttpRequest.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@CartaMenu, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: OkHttpResponse) {
                response.body?.let { responseBody ->
                    val jsonArray = JSONArray(responseBody.string())
                    productoCartas.clear()
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val producto = jsonObject.getString("producto")
                        val precio = jsonObject.getDouble("precio")
                        val imagen = jsonObject.getString("img")
                        val imagenUrl = "http://35.223.94.102/imagenes/$imagen"
                        productoCartas.add(ProductoCarta(producto, precio, imagenUrl))
                    }
                    runOnUiThread {
                        recyclerView.adapter?.notifyDataSetChanged()
                    }
                }
            }
        })
    }

    private fun cargarCategoriasEnMenu() {
        val url = "http://35.223.94.102/asi_sistema/android/categorias_menu.php"
        val request = OkHttpRequest.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@CartaMenu, "Error al cargar categorías", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onResponse(call: Call, response: OkHttpResponse) {
                val body = response.body?.string() ?: return
                val jsonArray = JSONArray(body)

                runOnUiThread {
                    val menu = navigationView.menu
                    val groupId = Menu.FIRST + 1
                    menu.addSubMenu("Categorías")

                    for (i in 0 until jsonArray.length()) {
                        val categoria = jsonArray.getString(i)
                        menu.add(groupId, Menu.FIRST + 10 + i, Menu.NONE, categoria)
                    }
                }
            }
        })
    }

    private fun fetchProductosPorCategoria(categoria: String) {
        val url = "http://35.223.94.102/asi_sistema/android/menu.php?categoria=$categoria"

        val request = OkHttpRequest.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@CartaMenu, "Error al cargar productos", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onResponse(call: Call, response: OkHttpResponse) {
                val body = response.body?.string() ?: return
                val jsonArray = JSONArray(body)

                productoCartas.clear()
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val producto = jsonObject.getString("producto")
                    val precio = jsonObject.getDouble("precio")
                    val imagen = jsonObject.getString("img")
                    val imagenUrl = "http://35.223.94.102/imagenes/$imagen"
                    productoCartas.add(ProductoCarta(producto, precio, imagenUrl))
                }

                runOnUiThread {
                    recyclerView.adapter?.notifyDataSetChanged()
                }
            }
        })
    }

    private fun verificarMostrarBoton() {
        val url = "http://35.223.94.102/asi_sistema/android/verificar_pedido.php"
        val request = object : StringRequest(Method.POST, url,
            { response ->
                try {
                    val json = JSONObject(response)
                    val mostrar = json.getBoolean("mostrar")
                    val btnConfirmar = findViewById<Button>(R.id.btnConfirmarPedido)
                    btnConfirmar.visibility = if (mostrar) View.VISIBLE else View.GONE
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            { error ->
                error.printStackTrace()
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["usuario"] = nombreUsuario  // Asumiendo que 'nombreUsuario' es una variable global
                return params
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun confirmarPedidos() {
        val url = "http://35.223.94.102/asi_sistema/android/cambiar_estado_pedido.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                // Después de confirmar, enviamos la notificación
                enviarNotificacionPedido("UsuarioNombre", "ProductoNombre") //
                verificarMostrarBoton() // Actualizar la visibilidad del botón aquí
                              },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error al confirmar pedidos: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["accion"] = "confirmar_todos"
                return params
            }
        }

        Volley.newRequestQueue(this).add(stringRequest)
    }

    private fun enviarNotificacionPedido(usuario: String, producto: String) {
        val url = "http://35.223.94.102/asi_sistema/android/notificacion_confirmar_pedido.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                Toast.makeText(this, "Notificación enviada", Toast.LENGTH_SHORT).show()
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error al enviar notificación: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["titulo"] = "Nuevo pedido"
                params["mensaje"] = "$usuario ha pedido $producto"
                return params
            }
        }

        Volley.newRequestQueue(this).add(stringRequest)
    }











    private fun actualizarContadorCarrito(usuario: String) {
        val url = "http://35.223.94.102/asi_sistema/android/carrito.php"
        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                val json = JSONObject(response)
                val cantidad = json.getInt("cantidad")

                val cartItemCount = findViewById<TextView>(R.id.cartItemCount)
                val imgCarrito = findViewById<ImageView>(R.id.imgCarrito)

                if (cantidad > 0) {
                    cartItemCount.text = cantidad.toString()
                    cartItemCount.visibility = View.VISIBLE
                    imgCarrito.visibility = View.VISIBLE  // Hacer visible el ImageView si la cantidad es mayor a 0

                } else {
                    cartItemCount.visibility = View.GONE
                    imgCarrito.visibility = View.GONE  // Ocultar el ImageView si la cantidad es 0 o menos

                }
            },
            { error ->
                error.printStackTrace()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["usuario"] = usuario // Usa el usuario correcto
                return params
            }
        }

        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    ///funcion para agregar productos
    // Dentro de la clase CartaMenu
// Definir la cantidad

        /** implementación de la interfaz: */
    /** Adapter listener: muestra el diálogo para cantidad */
    override fun mostrarDialogoCantidad(producto: ProductoCarta) {
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            hint = "Cantidad"
        }
        AlertDialog.Builder(this)
            .setTitle("Ingrese la cantidad")
            .setMessage("¿Cuántas unidades de ${producto.producto} desea agregar?")
            .setView(input)
            .setPositiveButton("Aceptar") { dialog, _ ->
                val cantidad = input.text.toString().toIntOrNull() ?: 0
                if (cantidad > 0) {
                    enviarProductoAPI(
                        this,
                        nombreUsuario,
                        producto.producto,
                        cantidad,
                        producto.precio
                    )

                    mostrarBtnConfirmarPedido()
                    // Aquí actualizas el contador del carrito
                    actualizarContadorCarrito(cantidad)
                } else {
                    Toast.makeText(this, "Cantidad inválida", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }


 fun mostrarBtnConfirmarPedido() {
        // Muestra el botón de Confirmar Pedido después de que se ingrese la cantidad
        btnConfirmarPedido.visibility = View.VISIBLE
    }


    fun actualizarContadorCarrito(cantidad: Int) {
        // Aquí actualizas el TextView que muestra la cantidad
        val cartItemCount = findViewById<TextView>(R.id.cartItemCount)
        val currentCount = cartItemCount.text.toString().toIntOrNull() ?: 0
        cartItemCount.text = (currentCount + cantidad).toString()  // Actualizas el contador
    }
// Llamar a la función pasando la cantidad

    private fun enviarProductoAPI(
        context: Context,
        usuario: String,
        producto: String,
        cantidad: Int,
        precio: Double
    ) {
        val url = "http://35.223.94.102/asi_sistema/android/agregar_producto.php"
        val stringRequest = object : StringRequest(
            Request.Method.POST,
            url,
            Response.Listener { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getBoolean("success")
                    val message = jsonResponse.getString("message")
                    if (success) {
                        Toast.makeText(context, "Producto agregado exitosamente", Toast.LENGTH_SHORT).show()
                        actualizarContadorCarrito(usuario)
                    } else {
                        Toast.makeText(context, "Error: $message", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(context, "Error al procesar respuesta: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(context, "Error al agregar producto: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): Map<String, String> = hashMapOf(
                "usuario" to usuario,
                "producto" to producto,
                "cantidad" to cantidad.toString(),
                "precio" to precio.toString(),
                "total" to (cantidad * precio).toString(),
                "estado" to "0",
                "delivery" to "no",
                "metodo_pago" to "default"
            )
        }
        Volley.newRequestQueue(context).add(stringRequest)
    }

    private fun handleNavigation(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.menu_inicio -> {
                supportActionBar?.title = "Inicio"
                fetchProductos()
            }
            R.id.menu_salir -> finish()
            else -> {
                val categoria = menuItem.title.toString()
                supportActionBar?.title = categoria
                fetchProductosPorCategoria(categoria)
            }
        }
    }
}



