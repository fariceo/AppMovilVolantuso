    package com.elrancho.cocina.ordenes.ver_pedidos

    import android.os.Bundle
    import android.widget.Toast
    import androidx.appcompat.app.AppCompatActivity
    import androidx.recyclerview.widget.LinearLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import com.android.volley.Request
    import com.android.volley.toolbox.JsonArrayRequest
    import com.android.volley.toolbox.Volley
    import com.elrancho.cocina.R
    import android.content.Context
    import android.app.NotificationManager
    import android.app.NotificationChannel
    import androidx.core.app.NotificationCompat
    import com.google.firebase.FirebaseApp
    import com.google.firebase.messaging.FirebaseMessaging
    import com.android.volley.toolbox.StringRequest
    import android.os.Build
    import androidx.core.content.ContextCompat
    import androidx.core.app.ActivityCompat
    import com.bumptech.glide.Glide







    class VerPedidosActivity : AppCompatActivity(), PedidoAgrupadoAdapter.OnFiadoClickListener {

        private lateinit var recyclerView: RecyclerView
        private lateinit var adapter: PedidoAgrupadoAdapter
        private val listaAgrupada = mutableListOf<PedidoAgrupado>()


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_ver_pedidos)


            solicitarPermisoDeNotificaciones() // 👈 Aquí
            // PEDIR PERMISO DE NOTIFICACIONES (para Android 13+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                        1
                    )
                }
            }

            // Inicializar Firebase
            FirebaseApp.initializeApp(this)

            recyclerView = findViewById(R.id.recyclerPedidosAgrupados)
            recyclerView.layoutManager = LinearLayoutManager(this)

            // Suscribirse a topic para notificaciones push
            FirebaseMessaging.getInstance().subscribeToTopic("fiado")
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        //Log.d("FCM", "Dispositivo suscrito al topic FIADO")
                    }
                }
            obtenerPedidosAgrupados()
        }

            private fun obtenerPedidosAgrupados() {
                //val url = "https://elpollovolantuso.com/asi_sistema/android/pedidos_agrupados.php"
                val url = "http://35.223.94.102/asi_sistema/android/pedidos_agrupados.php"

                val request = JsonArrayRequest(Request.Method.GET, url, null,
                    { response ->
                        listaAgrupada.clear()

                        for (i in 0 until response.length()) {
                            val obj = response.getJSONObject(i)
                            val productosArray = obj.getJSONArray("productos")

                            val listaProductos = mutableListOf<DetalleProducto>()
                            for (j in 0 until productosArray.length()) {
                                val p = productosArray.getJSONObject(j)
                                val producto = DetalleProducto(
                                    producto = p.getString("producto"),
                                    cantidad = p.getInt("cantidad"),
                                    precio = p.getDouble("precio"),
                                    total = p.getDouble("total"),
                                    fecha = p.getString("fecha"),
                                    delivery_type = p.getString("delivery"),
                                    delivery_cost = p.getDouble("delivery_cost")
                                )
                                listaProductos.add(producto)
                            }

                            val pedido = PedidoAgrupado(
                                id = obj.getInt("id"), // 👈 Aquí extraes el ID
                                usuario = obj.getString("usuario"),
                                total = obj.getDouble("total"),
                                fecha = obj.getString("fecha"),
                                productos = listaProductos,
                                metodo_pago = obj.getString("metodo_pago") // 👈 extraer del JSON
                            )
                            listaAgrupada.add(pedido)
                        }

                        adapter = PedidoAgrupadoAdapter(listaAgrupada, this) // Pasa el listener
                        recyclerView.adapter = adapter

                    },
                    { error -> error.printStackTrace() })

                Volley.newRequestQueue(this).add(request)
            }

        // Implementación del método de la interfaz

        override fun onAgregarFiado(saldo: Double, usuario: String, posicion: Int) {
            actualizarSaldoFiadoEnServidor(saldo, usuario)

            // Actualiza el objeto en la lista local
            listaAgrupada[posicion].metodo_pago = "Fiado"

            // Notifica al adapter que ese ítem ha cambiado
            adapter.notifyItemChanged(posicion)

        }

        private fun actualizarSaldoFiadoEnServidor(saldo: Double, usuario: String) {
            //val url = "https://elpollovolantuso.com/asi_sistema/android/actualizar_saldo_pendiente.php"
            val url = "http://35.223.94.102/asi_sistema/android/actualizar_saldo_pendiente.php"

            // Obtener la fecha y hora actuales
            val fechaActual = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
            val horaActual = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())


            val request = object : com.android.volley.toolbox.StringRequest(Method.POST, url,
                { response ->
                    if (response.trim() == "success") {
                        Toast.makeText(this, "Saldo fiado agregado correctamente", Toast.LENGTH_SHORT).show()


                        // Aquí actualizas el saldo y mandas la notificación

                        enviarNotificacionPush(usuario, saldo)
                    } else {
                        Toast.makeText(this, "Error desde el servidor: $response", Toast.LENGTH_LONG).show()
                    }
                },
                { error ->
                    Toast.makeText(this, "Error al agregar saldo: ${error.localizedMessage ?: "Error desconocido"}", Toast.LENGTH_LONG).show()
                }
            ) {
                override fun getParams(): Map<String, String> {
                    return mapOf(
                        "usuario" to usuario,
                        "saldo" to saldo.toString(),
                        "accion" to "1",
                        "fecha" to fechaActual,
                        "hora" to horaActual
                    )
                }
            }

            Volley.newRequestQueue(this).add(request)
        }

        private fun enviarNotificacionPush(usuario: String, saldo: Double) {
            val url = "http://35.223.94.102/asi_sistema/android/notificacion_fcm.php"

            val request = object : StringRequest(
                Request.Method.POST, url,
                { response ->
                    if (response.trim() == "success") {
                        Toast.makeText(this, "Notificación enviada correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Error del servidor: $response", Toast.LENGTH_LONG).show()
                    }
                },
                { error ->
                    // Manejo del error de red
                    val errorMsg = when {
                        error.networkResponse != null -> {
                            // Si tenemos una respuesta de red, mostramos el código de error y mensaje
                            val statusCode = error.networkResponse.statusCode
                            val responseBody = String(error.networkResponse.data)
                            "Código de error: $statusCode, Respuesta: $responseBody"
                        }
                        error.localizedMessage != null -> {
                            // Si hay un mensaje localizado del error
                            error.localizedMessage
                        }
                        else -> {
                            // En caso de que no haya información específica del error
                            "Error desconocido"
                        }
                    }

                    // Muestra el error en un Toast
                    Toast.makeText(this, "Error de red al enviar notificación: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            ) {
                override fun getParams(): Map<String, String> {
                    return mapOf(
                        "usuario" to usuario,
                        "saldo" to saldo.toString()
                    )
                }
            }

            Volley.newRequestQueue(this).add(request)
        }
        private fun solicitarPermisoDeNotificaciones() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {

                    // Mostrar explicación al usuario
                    val builder = androidx.appcompat.app.AlertDialog.Builder(this)
                    builder.setTitle("Permiso para notificaciones")
                    builder.setMessage("Esta app necesita permiso para enviarte notificaciones sobre pedidos importantes, como los fiados o listos para entrega.")
                    builder.setPositiveButton("Permitir") { dialog, _ ->
                        // Solicitar permiso
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                            1
                        )
                        dialog.dismiss()
                    }
                    builder.setNegativeButton("Cancelar") { dialog, _ ->
                        dialog.dismiss()
                    }
                    builder.show()
                }
            }
        }

        override fun onPedidoListo(usuario: String, total: Double,delivery_type: String, metodoPago: String) {
            //val url = "https://elpollovolantuso.com/asi_sistema/android/pedidos_listo.php"
            val url = "http://35.223.94.102/asi_sistema/android/pedidos_listo.php"

            val request = object : com.android.volley.toolbox.StringRequest(Method.POST, url,
                { response ->
                    if (response.trim() == "success") {
                        // ACTUALIZAR EL RECYCLER VIEW
                        // 👉 Aquí llamas a la función del adaptador para eliminar el pedido
                        adapter.eliminarPedidoPorUsuario(usuario)
                        Toast.makeText(this, "Pedido de $usuario marcado como listo", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Error al marcar pedido como listo: $response", Toast.LENGTH_SHORT).show()
                    }
                },
                { error ->
                    Toast.makeText(this, "Error en la solicitud: ${error.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            ) {
                override fun getParams(): Map<String, String> {
                    return mapOf(
                        "usuario" to usuario,
                        "total" to total.toString(),
                        "delivery" to delivery_type,
                        "metodo_pago" to metodoPago  // ✅ ahora sí se envía

                    )

                }
            }

            Volley.newRequestQueue(this).add(request)
        }

    }
