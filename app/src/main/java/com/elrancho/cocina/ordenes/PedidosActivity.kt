
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

import android.content.Intent

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.elrancho.cocina.MainActivity

class PedidosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductoAdapter
    private lateinit var cantidadEditText: EditText
    private lateinit var totalTextView: TextView
    private lateinit var confirmarBtn: Button
    private lateinit var buscarEditText: EditText

    private var productoSeleccionado: Producto? = null
    private var usuarioSeleccionado: UsuarioSaldo? = null
    private lateinit var editTextBuscarUsuario: EditText
    private lateinit var recyclerViewUsuarios: RecyclerView
    private lateinit var textViewSaldo: TextView
    private var usuarioYaSeleccionado = false // ✅ Aquí
    private var nombreUsuarioSeleccionado: String? = null
    private lateinit var logoImageView: ImageView
    private lateinit var imagenDesdeUrl: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedidos)

        textViewSaldo = findViewById(R.id.textViewSaldoPendiente)
        buscarEditText = findViewById(R.id.editTextBuscarProducto)
        recyclerView = findViewById(R.id.recyclerViewProductos)
        cantidadEditText = findViewById(R.id.editTextCantidad)
        totalTextView = findViewById(R.id.textViewTotal)
        confirmarBtn = findViewById(R.id.botonConfirmar)

        editTextBuscarUsuario = findViewById(R.id.editTextBuscarUsuario)
        recyclerViewUsuarios = findViewById(R.id.recyclerViewUsuarios)
        recyclerViewUsuarios.layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = LinearLayoutManager(this)

        logoImageView = findViewById(R.id.logoImageView)
        imagenDesdeUrl = findViewById(R.id.imagenDesdeUrl)
        // Buscar productos al escribir
        buscarEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length >= 2) {
                    buscarProducto(s.toString())
                }
            }
        })

        // Buscar usuarios al escribir
        editTextBuscarUsuario.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val texto = s.toString()
                // Si el usuario cambió el texto después de seleccionar, reinicia búsqueda
                if (usuarioYaSeleccionado && texto != nombreUsuarioSeleccionado) {
                    usuarioYaSeleccionado = false
                    recyclerViewUsuarios.adapter = null
                    recyclerViewUsuarios.visibility = RecyclerView.GONE
                }

                if (!usuarioYaSeleccionado && texto.length >= 2) {
                    buscarUsuarios(texto)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })



        // Escuchar cambios de cantidad
        cantidadEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                actualizarTotal()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Confirmar pedido
        confirmarBtn.setOnClickListener {
            registrarPedido()
        }

        // Configurar el OnClickListener para el logo
        logoImageView.setOnClickListener {
            // Regresar a MainActivity
            finish() // Esto cierra la actividad actual y regresa a la anterior
        }

        // Cargar la imagen desde una URL usando Glide
        val urlImagen ="https://thumbs.dreamstime.com/b/vector-de-icono-l%C3%ADnea-lista-pedidos-la-%C3%B3rdenes-vectores-archivos-f%C3%A1cil-editar-277106601.jpg"
        Glide.with(this)
            .load(urlImagen)
            .into(imagenDesdeUrl)

        // Configurar el OnClickListener para la imagen desde la URL (navegar a otra actividad)
        imagenDesdeUrl.setOnClickListener {
            // Abrir una nueva actividad
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    private fun buscarUsuarios(nombre: String) {
        val url = "https://elpollovolantuso.com/asi_sistema/android/buscar_usuario.php?nombre=$nombre"
        val request = StringRequest(com.android.volley.Request.Method.GET, url, { response ->
            val usuariosJson = JSONArray(response)
            val listaUsuarios = mutableListOf<UsuarioSaldo>()

            for (i in 0 until usuariosJson.length()) {
                val item = usuariosJson.getJSONObject(i)
                val nombreUsuario = item.getString("usuario")
                val saldo = item.getDouble("saldo")
                listaUsuarios.add(UsuarioSaldo(nombreUsuario, saldo))
            }

            recyclerViewUsuarios.adapter = UsuarioAdapter(listaUsuarios) { usuario ->
                editTextBuscarUsuario.setText(usuario.nombre)
                nombreUsuarioSeleccionado = usuario.nombre
                textViewSaldo.text = "Saldo pendiente: $%.2f".format(usuario.saldo)

                usuarioSeleccionado = usuario // ✅ ESTA LÍNEA ES CLAVE

                recyclerViewUsuarios.visibility = RecyclerView.GONE
                usuarioYaSeleccionado = true
            }



            recyclerViewUsuarios.visibility = RecyclerView.VISIBLE // Mostrar lista de usuarios

        }, {
            Toast.makeText(this, "Error al buscar usuario", Toast.LENGTH_SHORT).show()
        })

        Volley.newRequestQueue(this).add(request)
    }

    private fun buscarProducto(nombre: String) {
        val url = "https://elpollovolantuso.com/asi_sistema/android/buscar_menu.php?nombre=$nombre"
        val request = StringRequest(com.android.volley.Request.Method.GET, url, { response ->
            val productos = JSONArray(response)
            val lista = mutableListOf<Producto>()
            for (i in 0 until productos.length()) {
                val item = productos.getJSONObject(i)
                val nombreProducto = item.getString("producto")
                val precio = item.getDouble("precio")
                lista.add(Producto(nombreProducto, precio))
            }
            adapter = ProductoAdapter(lista) { producto ->
                productoSeleccionado = producto
                buscarEditText.setText(producto.nombre) // Establecer el texto del EditText
                actualizarTotal() // Asegurarse de que el total se actualice
            }
            recyclerView.adapter = adapter
            recyclerView.visibility = RecyclerView.VISIBLE // Asegura que el RecyclerView se muestre
        }, {
            Toast.makeText(this, "Error al buscar producto", Toast.LENGTH_SHORT).show()
        })

        Volley.newRequestQueue(this).add(request)
    }

    private fun actualizarTotal() {
        // Verifica si se tiene un producto seleccionado y una cantidad válida
        val cantidad = cantidadEditText.text.toString().toIntOrNull() ?: 0
        val precio = productoSeleccionado?.precio ?: 0.0

        // Evitar que se calcule un total si no hay producto seleccionado o si la cantidad es 0
        if (productoSeleccionado != null && cantidad > 0) {
            val total = cantidad * precio
            totalTextView.text = "Total: $%.2f".format(total)
        } else {
            totalTextView.text = "Total: $0.00"
        }
    }

    private fun registrarPedido() {
        val cantidad = cantidadEditText.text.toString().toIntOrNull()
        val producto = productoSeleccionado ?: return
        val usuario = usuarioSeleccionado?.nombre ?: return

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
            // ✅ Limpiar campos
            buscarEditText.setText("")        // Limpiar búsqueda de producto
            cantidadEditText.setText("")     // Limpiar cantidad
            totalTextView.text = "Total: $0.00" // Reiniciar total
            productoSeleccionado = null      // Limpiar producto seleccionado
            // ✅ Limpiar RecyclerView
            adapter = ProductoAdapter(emptyList()) { }
            recyclerView.adapter = adapter
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
