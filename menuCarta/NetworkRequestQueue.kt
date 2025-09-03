package com.elrancho.cocina.menuCarta


//Configura Volley para realizar las solicitudes HTTP a tu API. // NetworkRequestQueue.kt
import com.android.volley.RequestQueue import com.android.volley.toolbox.Volley import android.content.Context
object NetworkRequestQueue {
    private var requestQueue: RequestQueue? = null
    fun getInstance(context: Context): RequestQueue { if (requestQueue == null) {
        requestQueue = Volley.newRequestQueue(context.applicationContext) }
        return requestQueue!! }
}