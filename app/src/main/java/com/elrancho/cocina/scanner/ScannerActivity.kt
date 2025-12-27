package com.elrancho.cocina.scanner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.elrancho.cocina.R
import com.journeyapps.barcodescanner.*
import com.google.zxing.BarcodeFormat
import org.json.JSONObject

class ScannerActivity : AppCompatActivity() {

    private lateinit var barcodeView: DecoratedBarcodeView
    private val CAMERA_REQUEST_CODE = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        barcodeView = findViewById(R.id.barcodeScanner)

        iniciarScanner()
    }

    private fun iniciarScanner() {
        barcodeView.barcodeView.decoderFactory =
            DefaultDecoderFactory(BarcodeFormat.values().toList())

        barcodeView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult) {
                val codigo = result.text ?: return

                buscarProducto(codigo)
            }
        })
    }

    private fun buscarProducto(codigo: String) {
        // Obtener la BD del negocio desde SharedPreferences
        val prefs = getSharedPreferences("user_session", MODE_PRIVATE)
        val nombreBD = prefs.getString("nombre_bd", "volantuso") ?: "volantuso"

        val url = "https://elpollovolantuso.com/negocioencontrol/api/buscar_producto_codigo.php"

        val request = object : StringRequest(
            Request.Method.POST,
            url,
            { response ->
                try {
                    val json = JSONObject(response)
                    if (json.getBoolean("success")) {
                        val producto = json.getJSONObject("producto")
                        Toast.makeText(
                            this,
                            "✔ ${producto.getString("producto")}\n💲 ${producto.getDouble("precio")}\n📦 ${producto.getInt("stock_inicial")}",
                            Toast.LENGTH_LONG
                        ).show()

                        // Devolver el código al MainActivity/PedidosActivity
                        val intent = Intent()
                        intent.putExtra("codigo", codigo)
                        intent.putExtra("producto", producto.getString("producto"))
                        intent.putExtra("precio", producto.getDouble("precio"))
                        setResult(Activity.RESULT_OK, intent)
                        barcodeView.pause()
                        finish()

                    } else {
                        Toast.makeText(this, json.getString("msg"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error JSON", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf(
                    "codigo" to codigo,
                    "nombre_bd" to nombreBD
                )
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    override fun onResume() {
        super.onResume()
        barcodeView.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause()
    }
}
