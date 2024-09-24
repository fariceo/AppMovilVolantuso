package com.elrancho.cocina.menuCarta

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

// Datos necesarios para el envío de datos
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.elrancho.cocina.R
import org.json.JSONObject

class CartaMenuAdapter(
    private val productoCartas: List<ProductoCarta>
) : RecyclerView.Adapter<CartaMenuAdapter.ProductoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cartamenu, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productoCartas[position]
        holder.tvcartaProducto.text = producto.producto
        holder.tvcartaPrecio.text = "$ ${producto.precio}"

        // Manejo del clic en el botón agregar_producto
        holder.agregarProducto.setOnClickListener {
            mostrarAlerta(holder.itemView.context, producto)
        }
    }

    override fun getItemCount() = productoCartas.size

    // Mostrar alerta cuando se hace clic en el botón
    private fun mostrarAlerta(context: Context, producto: ProductoCarta) {
        // Crear un EditText para ingresar la cantidad
        val inputCantidad = EditText(context)
        inputCantidad.hint = "Ingrese la cantidad"

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Agregar producto")
        builder.setMessage("Producto: ${producto.producto} \nPrecio: $${producto.precio}")
        builder.setView(inputCantidad)  // Añadir el campo de entrada de cantidad

        // Botón "Aceptar"
        builder.setPositiveButton("Aceptar") { dialog, _ ->
            val cantidadStr = inputCantidad.text.toString()

            if (cantidadStr.isNotEmpty() && cantidadStr.toIntOrNull() != null) {
                val cantidad = cantidadStr.toInt()

                // Realizar el envío de los datos a la API REST
                enviarDatosAPI(context, producto.producto, producto.precio, cantidad)

                Toast.makeText(context, "Has agregado $cantidad de ${producto.producto}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Por favor, ingrese una cantidad válida.", Toast.LENGTH_SHORT).show()
            }

            dialog.dismiss()
        }

        // Botón "Cancelar"
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    /// Función de envío de datos a la API REST
    private fun enviarDatosAPI(context: Context, producto: String, precio: Double, cantidad: Int) {
           val url = "https://elpollovolantuso.com/asi_sistema/android/pedidos_android.php"  // Cambia esta URL por la de tu API

        val queue = Volley.newRequestQueue(context)

        val jsonBody = JSONObject()
        jsonBody.put("producto", producto)
        jsonBody.put("precio", precio)
        jsonBody.put("cantidad", cantidad)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, jsonBody,
            Response.Listener { response ->
                // Mostrar mensaje de éxito tras recibir respuesta de la API
                Toast.makeText(context, "Datos enviados exitosamente: ${response.getString("mensaje")}", Toast.LENGTH_SHORT).show()
            },
            Response.ErrorListener { error ->
                // Mostrar mensaje de error si ocurre algún problema al enviar los datos
                Toast.makeText(context, "Error al enviar datos: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        // Añadir la solicitud a la cola de peticiones de Volley
        queue.add(jsonObjectRequest)
    }

    class ProductoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvcartaProducto: TextView = view.findViewById(R.id.tvcartaProducto)
        val tvcartaPrecio: TextView = view.findViewById(R.id.tvcartaPrecio)
        val agregarProducto: Button = view.findViewById(R.id.agregar_producto)
    }
}
