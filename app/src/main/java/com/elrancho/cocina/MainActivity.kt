package com.elrancho.cocina

import android.content.ClipData.Item
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager

import com.elrancho.cocina.consultaMysql.consulta

class MainActivity : AppCompatActivity() {
    private lateinit var usuario: EditText
    private lateinit var producto: EditText
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        usuario = findViewById(R.id.usuario)
        producto = findViewById(R.id.producto)


        ///con este boton vamos a la actividad menu
        val buttonNavigate: Button = findViewById(R.id.menu)
        buttonNavigate.setOnClickListener {
            // Create an Intent to start SecondActivity
            val intent = Intent(this, menu::class.java)
            startActivity(intent)
        }



        ///con este boton vamos a la actividad consulta
        val buttonNavigate3: Button = findViewById(R.id.consulta)
        buttonNavigate3.setOnClickListener {
            // Create an Intent to start SecondActivity
            val intent = Intent(this, consulta::class.java)
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
///fwewfwefwedfewfdewfwefewfew

        ///mañana tendre mucho dinero


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


                        //josjadjaoj
                    }
                }
            }
        })
    }
}