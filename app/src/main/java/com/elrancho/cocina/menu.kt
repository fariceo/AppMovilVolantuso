package com.elrancho.cocina

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Usuario(val id: Int, val usuario: String, val producto: String)

class menu : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        textView = findViewById(R.id.algo)
        requestQueue = Volley.newRequestQueue(this)

        fetchUsuarios()
    }

    private fun fetchUsuarios() {
        val url = "https://elpollovolantuso.com/testing.php"

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                val gson = Gson()
                val listType = object : TypeToken<List<Usuario>>() {}.type
                val usuarios: List<Usuario> = gson.fromJson(response.toString(), listType)
                displayUsuarios(usuarios)
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )

        requestQueue.add(jsonArrayRequest)
    }

    private fun displayUsuarios(usuarios: List<Usuario>) {
        val builder = StringBuilder()
        for (usuario in usuarios) {
            builder.append("ID: ${usuario.id}, usuario: ${usuario.usuario}, producto: ${usuario.producto}\n")
        }
        textView.text = builder.toString()
    }
}
