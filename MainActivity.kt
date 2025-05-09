package com.elrancho.cocina

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.elrancho.cocina.compras.GastosActivity
import com.elrancho.cocina.consultaMysql.consulta
import com.elrancho.cocina.menuCarta.CartaMenu
import com.elrancho.cocina.ordenes.PedidosActivity
import com.elrancho.cocina.usuarios.login.LoginActivity
import com.elrancho.cocina.deudas.PagosActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var usuario: EditText
    private lateinit var producto: EditText
    private val client = OkHttpClient()
    private lateinit var btnIngresarOrden: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        usuario = findViewById(R.id.usuario)
        producto = findViewById(R.id.producto)
        btnIngresarOrden = findViewById(R.id.btnIngresarOrden)

        // Cargar imágenes con Glide para los botones
        setupImageButtons()

        // Verificar sesión de usuario
       // checkUserSession()

        // Configurar el botón para redirigir a GastosActivity
        val btnVerGastos = findViewById<ImageButton>(R.id.btnVerGastos)
        setupGlideForImageButton(btnVerGastos, "http://35.223.94.102/imagenes/carrito.png")
        btnVerGastos.setOnClickListener {
            startActivity(Intent(this, GastosActivity::class.java))
        }

        // Configurar el botón para redirigir a PedidosActivity
        setupGlideForImageButton(btnIngresarOrden, "http://35.223.94.102/imagenes/camarero.jpeg")
        btnIngresarOrden.setOnClickListener {
            startActivity(Intent(this@MainActivity, PedidosActivity::class.java))
        }

        // Botón para ir a la actividad de login
        val buttonNavigate1: Button = findViewById(R.id.iralogin)
        buttonNavigate1.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Configurando el botón para ir a la actividad consulta Ventas
        val btnventas = findViewById<ImageButton>(R.id.consulta)
        setupGlideForImageButton(btnventas, "http://35.223.94.102/imagenes/historial.png")
        btnventas.setOnClickListener {
            startActivity(Intent(this, consulta::class.java))
        }

        // Botón para ir a la actividad carta
        val btnCarta = findViewById<ImageButton>(R.id.carta)
        setupGlideForImageButton(btnCarta, "http://35.223.94.102/imagenes/carta.png")
        btnCarta.setOnClickListener {
            startActivity(Intent(this, CartaMenu::class.java))
        }

        // Configurando el botón para ir a la actividad Pagos
        val btnSaldoPendiente = findViewById<ImageButton>(R.id.btnSaldoPendiente)
        setupGlideForImageButton(btnSaldoPendiente, "http://35.223.94.102/imagenes/pago.png")
        btnSaldoPendiente.setOnClickListener {
            startActivity(Intent(this, PagosActivity::class.java))
        }
    }

    private fun setupImageButtons() {
        val btnIngresarOrden = findViewById<ImageButton>(R.id.btnIngresarOrden)
        val btnVerGastos = findViewById<ImageButton>(R.id.btnVerGastos)
        val btnSaldoPendiente = findViewById<ImageButton>(R.id.btnSaldoPendiente)
        val btnventas = findViewById<ImageButton>(R.id.consulta)
        val btncarta = findViewById<ImageButton>(R.id.carta)

        setupGlideForImageButton(btnVerGastos, "http://35.223.94.102/imagenes/carrito.png")
        setupGlideForImageButton(btnventas, "http://35.223.94.102/imagenes/historial.png")
        setupGlideForImageButton(btnSaldoPendiente, "http://35.223.94.102/imagenes/pago.png")
        setupGlideForImageButton(btnIngresarOrden, "http://35.223.94.102/imagenes/camarero.jpeg")
        setupGlideForImageButton(btncarta, "http://35.223.94.102/imagenes/carta.png")

    }

    private fun setupGlideForImageButton(imageButton: ImageButton, imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .override(100, 100) // Establece un tamaño de 100x100 píxeles
            .into(imageButton)
    }

    /*
    private fun checkUserSession() {
        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (!isLoggedIn) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
*/
    fun guardar(view: View) {
        val url = "https://elpollovolantuso.com/testing.php"
        val usuarioText = usuario.text.toString()
        val productoText = producto.text.toString()

        val formBody = FormBody.Builder()
            .add("usuario", usuarioText)
            .add("producto", productoText)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(applicationContext, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val responseData = responseBody.string()
                    val json = JSONObject(responseData)
                    runOnUiThread {
                        val message = if (json.getBoolean("success")) {
                            "Datos insertados correctamente"
                        } else {
                            "Error: ${json.getString("message")}"
                        }
                        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }
}
