package com.elrancho.cocina.usuarios.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.elrancho.cocina.MainActivity
import com.elrancho.cocina.R
import com.elrancho.cocina.menuCarta.CartaMenu
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernameField = findViewById<EditText>(R.id.username)
        val passwordField = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.loginButton)

        //registrar usuario
        val btnIrAregistrarUsuario = findViewById<Button>(R.id.btnIrAregistrar_usuario)

        btnIrAregistrarUsuario.setOnClickListener {
            val intent = Intent(this, RegistroUsuarioActivity::class.java)
            startActivity(intent)
        }


        loginButton.setOnClickListener {
            val username = usernameField.text.toString().trim() // Obtén el texto del EditText
            val password = passwordField.text.toString().trim()
            login(username, password)
        }




    }

    private fun login(username: String, password: String) {
        val url = "https://elpollovolantuso.com/asi_sistema/android/login.php"
        val queue = Volley.newRequestQueue(this)

        val request = object : StringRequest(Request.Method.POST, url, { response ->
            val jsonResponse = JSONObject(response)
            if (jsonResponse.getBoolean("success")) {

                val rol = jsonResponse.getString("rol") // Obtenemos el rol desde el JSON

                // Guardamos en SharedPreferences
                val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
                sharedPreferences.edit()
                    .putBoolean("isLoggedIn", true)
                    .putString("username", username)
                    .putString("rol", rol) // Guardamos también el rol
                    .apply()

                // Redirigimos según el rol
                when (rol) {
                    "admin" -> startActivity(Intent(this, MainActivity::class.java))
                   // "cocina" -> startActivity(Intent(this, CocinaActivity::class.java))
                    //"motorizado" -> startActivity(Intent(this, MotorizadoActivity::class.java))
                    "cliente" -> startActivity(Intent(this, CartaMenu::class.java))
                    else -> {
                        Toast.makeText(this, "Rol desconocido: $rol", Toast.LENGTH_SHORT).show()
                        //return@StringRequest
                    }
                }
                finish()

            } else {
                Toast.makeText(this, "Login fallido", Toast.LENGTH_SHORT).show()
            }
        }, { error ->
            Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
        }) {
            override fun getParams(): Map<String, String> {
                return mapOf("username" to username, "password" to password)
            }
        }

        queue.add(request)
    }
}
