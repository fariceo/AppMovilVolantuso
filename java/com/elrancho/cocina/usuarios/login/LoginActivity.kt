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
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernameField = findViewById<EditText>(R.id.username)
        val passwordField = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            val username = usernameField.text.toString().trim() // Obtén el texto del EditText
            val password = passwordField.text.toString().trim()
            login(username, password)
        }

        val btnIrAMain = findViewById<Button>(R.id.btnIrAMain)
        btnIrAMain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    private fun login(username: String, password: String) {
        val url = "https://elpollovolantuso.com/asi_sistema/usuarios/login.php"
        val queue = Volley.newRequestQueue(this)

        val request = object : StringRequest(Request.Method.POST, url, { response ->
            val jsonResponse = JSONObject(response)
            if (jsonResponse.getBoolean("success")) {
                // Guarda el nombre del usuario y el estado de sesión en SharedPreferences
                val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
                sharedPreferences.edit()
                    .putBoolean("isLoggedIn", true)
                    .putString("username", username)  // Guarda el nombre de usuario
                    .apply()

                // Redirige a MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
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
