package com.elrancho.cocina

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.elrancho.cocina.compras.GastosActivity
import com.elrancho.cocina.consultaMysql.consulta
import com.elrancho.cocina.menuCarta.CartaMenu
import com.elrancho.cocina.ordenes.PedidosActivity
import com.elrancho.cocina.usuarios.login.LoginActivity
import com.elrancho.cocina.deudas.PagosActivity
import okhttp3.*
import android.widget.TextView
import com.elrancho.cocina.Motorizado.delivery.CompartirUbicacionActivity
import com.elrancho.cocina.Motorizado.delivery.DeliveryActivity
import com.elrancho.cocina.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private lateinit var btnIngresarOrden: ImageButton
    private lateinit var btnVerGastos: ImageButton
    private lateinit var btnSaldoPendiente: ImageButton
    private lateinit var btnventas: ImageButton
    private lateinit var btnCarta: ImageButton
    private lateinit var textViewSaludo: TextView

    private lateinit var btnIrDelivery: Button
    private lateinit var btnCompartirUbicacion: Button

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar botones
        btnIngresarOrden = findViewById(R.id.btnIngresarOrden)
        btnVerGastos = findViewById(R.id.btnVerGastos)
        btnSaldoPendiente = findViewById(R.id.btnSaldoPendiente)
        btnventas = findViewById(R.id.consulta)
        btnCarta = findViewById(R.id.carta)
        btnIrDelivery = findViewById(R.id.ir_delivery) // ← Asignado correctamente
        btnCompartirUbicacion = findViewById(R.id.compartir_ubicacion) // ← Asignado correctamente


        // Inicializar saludo
        textViewSaludo = findViewById(R.id.textViewSaludo)

        // Verificar sesión de usuario
        checkUserSession()

        // Mostrar saludo
        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "Usuario desconocido")
        textViewSaludo.text = "Bienvenido, $username"

        // Cargar imágenes con Glide
        setupImageButtons()

        // Configurar clics de botones
        btnVerGastos.setOnClickListener {
            startActivity(Intent(this, GastosActivity::class.java))
        }

        btnIngresarOrden.setOnClickListener {
            startActivity(Intent(this@MainActivity, PedidosActivity::class.java))
        }


        btnventas.setOnClickListener {
            startActivity(Intent(this, consulta::class.java))
        }

        btnCarta.setOnClickListener {
            startActivity(Intent(this, CartaMenu::class.java))
        }

        btnSaldoPendiente.setOnClickListener {
            startActivity(Intent(this, PagosActivity::class.java))
        }
        btnIrDelivery.setOnClickListener {
            //startActivity(Intent(this, DeliveryActivity::class.java))
            startActivity(Intent(this, DeliveryActivity::class.java))
        }

        btnCompartirUbicacion.setOnClickListener {
            //startActivity(Intent(this, DeliveryActivity::class.java))
            startActivity(Intent(this, CompartirUbicacionActivity::class.java))
        }
    }

    private fun setupImageButtons() {
        setupGlideForImageButton(btnVerGastos, "http://35.223.94.102/imagenes/carrito.png")
        setupGlideForImageButton(btnventas, "http://35.223.94.102/imagenes/historial.png")
        setupGlideForImageButton(btnSaldoPendiente, "http://35.223.94.102/imagenes/pago.png")
        setupGlideForImageButton(btnIngresarOrden, "http://35.223.94.102/imagenes/camarero.jpeg")
        setupGlideForImageButton(btnCarta, "http://35.223.94.102/imagenes/carta.png")
    }

    private fun setupGlideForImageButton(imageButton: ImageButton, imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .override(100, 100) // Establece un tamaño de 100x100 píxeles
            .into(imageButton)
    }

    private fun checkUserSession() {
        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (!isLoggedIn) {
            // Si no está logueado, ir al login y cerrar esta actividad
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return // Importante: detener ejecución aquí
        }

        // Si está logueado, seguir configurando
        val iralogin = findViewById<ImageButton>(R.id.iralogin)
        val urlLogin = "http://35.223.94.102/imagenes/usuarios.png"
        val urlCerrarSesion = "https://w7.pngwing.com/pngs/749/229/png-transparent-abmeldung-button-icon-shut-s-text-computer-sign.png"

        // Mostrar imagen de cerrar sesión
        Glide.with(this)
            .load(urlCerrarSesion)
            .into(iralogin)

        iralogin.setOnClickListener {
            // Cierra sesión y regresa al login
            sharedPreferences.edit().clear().apply()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

