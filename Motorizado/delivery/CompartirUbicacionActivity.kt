package com.elrancho.cocina.Motorizado.delivery

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.elrancho.cocina.R

class CompartirUbicacionActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap // Variable para almacenar el objeto del mapa
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var ubicacionActual: LatLng? = null
    private val PERMISO_UBICACION = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compartir_ubicacion)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Configuramos el mapa
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        findViewById<Button>(R.id.btnCompartirUbicacion).setOnClickListener {
            ubicacionActual?.let {
                // Llamamos a la función para enviar la ubicación si está disponible
                enviarUbicacionAlServidor(it.latitude, it.longitude)
            } ?: Toast.makeText(this, "Ubicación no disponible", Toast.LENGTH_SHORT).show()
        }


    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap // Inicializamos la variable map

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
            obtenerUbicacion()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISO_UBICACION
            )
        }
    }

    private fun obtenerUbicacion() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let { ubicacion ->
                ubicacionActual = LatLng(ubicacion.latitude, ubicacion.longitude)
                // Asegurarse de que ubicacionActual no es null antes de usarlo
                ubicacionActual?.let {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 17f))
                    map.addMarker(MarkerOptions().position(it).title("Tu ubicación"))
                }
            }
        }
    }


    private fun enviarUbicacionAlServidor(lat: Double, lng: Double) {
        val url = "http://35.223.94.102/asi_sistema/android/guardar_ubicacion.php"

        val stringRequest = object : StringRequest(
            Method.POST, url,
            { response ->
                Toast.makeText(this, "Ubicación compartida", Toast.LENGTH_SHORT).show()
            },
            { error ->
                Toast.makeText(this, "Error al enviar: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["usuario"] = "cliente123" // Reemplaza con el ID real del cliente
                params["latitud"] = lat.toString()
                params["longitud"] = lng.toString()
                return params
            }
        }

        Volley.newRequestQueue(this).add(stringRequest)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (requestCode == PERMISO_UBICACION && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            onMapReady(map)
        } else {
            Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }
}
