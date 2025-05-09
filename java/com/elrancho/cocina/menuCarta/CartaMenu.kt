package com.elrancho.cocina.menuCarta

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elrancho.cocina.R
import com.google.android.material.navigation.NavigationView
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class CartaMenu : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var recyclerView: RecyclerView
    private val productoCartas = mutableListOf<ProductoCarta>()
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carta_menu)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigationView)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.open_drawer, R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_inicio -> {
                    Toast.makeText(this, "Inicio seleccionado", Toast.LENGTH_SHORT).show()
                }
                R.id.menu_salir -> {
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        recyclerView = findViewById(R.id.recyclerView2)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = CartaMenuAdapter(productoCartas)
        recyclerView.addItemDecoration(
            DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        )

        fetchProductos()
    }

    private fun fetchProductos() {
        val request = Request.Builder()
            .url("http://35.223.94.102/asi_sistema/android/menu.php")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@CartaMenu, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val jsonArray = JSONArray(responseBody.string())
                    productoCartas.clear()
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val producto = jsonObject.getString("producto")
                        val precio = jsonObject.getDouble("precio")
                        val imagen = jsonObject.getString("img")
                        val imagenUrl = "http://35.223.94.102/imagenes/$imagen"
                        productoCartas.add(ProductoCarta(producto, precio, imagenUrl))
                    }
                    runOnUiThread {
                        recyclerView.adapter?.notifyDataSetChanged()
                    }
                }
            }
        })
    }
}
