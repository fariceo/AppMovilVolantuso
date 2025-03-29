package com.elrancho.cocina

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import com.elrancho.cocina.consultaMysql.consulta
import com.elrancho.cocina.menuCarta.CartaMenu
import com.elrancho.cocina.usuarios.login.LoginActivity

class MainActivity : AppCompatActivity() {
    private lateinit var usuario: EditText
    private lateinit var producto: EditText
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        usuario = findViewById(R.id.usuario)
        producto = findViewById(R.id.producto)

        // Recupera el nombre del usuario desde SharedPreferences
        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
/*
        if (isLoggedIn) {
            val username = sharedPreferences.getString("username", "Usuario")
            findViewById<TextView>(R.id.usernameTextView).text = "Bienvenido, $username"
        } else {
            // Si no ha iniciado sesión, redirige a LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
           // finish()
        }
*/
        // Botón para ir a la actividad consulta
        val buttonNavigate2: Button = findViewById(R.id.consulta)
        buttonNavigate2.setOnClickListener {
            val intent = Intent(this, consulta::class.java)
            startActivity(intent)
        }

        // Botón para ir a la actividad carta
        val buttonNavigate3: Button = findViewById(R.id.carta)
        buttonNavigate3.setOnClickListener {
            val intent = Intent(this, CartaMenu::class.java)
            startActivity(intent)
        }

        // Botón para ir a la actividad de login
        val buttonNavigate1: Button = findViewById(R.id.iralogin)
        buttonNavigate1.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

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
