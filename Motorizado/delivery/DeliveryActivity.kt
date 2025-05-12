package com.elrancho.cocina.Motorizado.delivery

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.elrancho.cocina.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class DeliveryActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var geocoder: Geocoder
    private lateinit var inputDireccion: EditText
    private lateinit var btnBuscarDireccion: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery)

        // Cargar mapa
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Inicializar Geocoder
        geocoder = Geocoder(this, Locale.getDefault())

        // Inicializar vistas
        inputDireccion = findViewById(R.id.inputDireccion)
        btnBuscarDireccion = findViewById(R.id.btnBuscarDireccion)

        // Configurar el botón para buscar dirección
        btnBuscarDireccion.setOnClickListener {
            val direccion = inputDireccion.text.toString()
            obtenerCoordenadasYMostrar(direccion)
        }
    }

        override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Configurar cámara en el mapa a una ubicación predeterminada
        val defaultLocation = LatLng(19.432608, -99.133209)  // Ciudad de México
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 14f))
    }

    private fun obtenerCoordenadasYMostrar(direccion: String) {
        try {
            val addresses = geocoder.getFromLocationName(direccion, 1) ?: emptyList()
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                val latLng = LatLng(address.latitude, address.longitude)

                // Mostrar el marcador en el mapa
                mMap.clear()
                mMap.addMarker(MarkerOptions().position(latLng).title("Dirección: $direccion"))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
            } else {
                showToast("Dirección no encontrada")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showToast("Error al obtener la dirección")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
