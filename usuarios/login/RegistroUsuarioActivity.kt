package com.elrancho.cocina.usuarios.login

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.elrancho.cocina.R
import android.content.Intent
import com.elrancho.cocina.MainActivity


class RegistroUsuarioActivity : AppCompatActivity() {

    private lateinit var editUsuario: EditText
    private lateinit var editNombre: EditText
    private lateinit var editPassword: EditText
    private lateinit var btnRegistrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_usuario)

        editUsuario = findViewById(R.id.editUsuario)
        editNombre = findViewById(R.id.editNombre)
        editPassword = findViewById(R.id.editPassword)
        btnRegistrar = findViewById(R.id.btnRegistrar)

        btnRegistrar.setOnClickListener {
            registrarUsuario()
        }
    }

    private fun registrarUsuario() {
        val usuario = editUsuario.text.toString().trim()
        val nombre = editNombre.text.toString().trim()
        val password = editPassword.text.toString().trim()

        if (usuario.isEmpty() || nombre.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "http://35.223.94.102/asi_sistema/android/registro_usuario.php"

        val queue = Volley.newRequestQueue(this)
        val request = object : StringRequest(Method.POST, url, { response ->
            Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()

            // Guarda el nombre del usuario y el estado de sesión en SharedPreferences
            val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
            sharedPreferences.edit()
                .putBoolean("isLoggedIn", true)
                .putString("username", usuario)  // Guarda el nombre de usuario
                .apply()
            // Ir a MainActivity directamente
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Opcional: cerrar la actividad después de registrar
        }, { error ->
            Toast.makeText(this, "Error al registrar: ${error.message}", Toast.LENGTH_SHORT).show()
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["usuario"] = usuario
                params["nombre"] = nombre
                params["password"] = password
                params["saldo"] = "0"
                return params
            }
        }

        queue.add(request)
    }
}
