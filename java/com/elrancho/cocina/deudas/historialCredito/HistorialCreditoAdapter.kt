package com.elrancho.cocina.deudas.historialCredito

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.elrancho.cocina.R

class HistorialCreditoAdapter(private val lista: List<HistorialCredito>) :
    RecyclerView.Adapter<HistorialCreditoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtFecha: TextView = view.findViewById(R.id.txtFecha)
        val txtSaldo: TextView = view.findViewById(R.id.txtSaldo)  // Muestra el saldo
        val txtSaldoContable: TextView = view.findViewById(R.id.txtSaldoContable)  // Muestra el saldo contable
        val txtUsuario: TextView = view.findViewById(R.id.txtUsuario)  // Muestra el nombre del usuario
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial_credito, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]

        // Asignamos los datos a los TextViews correspondientes
        holder.txtFecha.text = item.fecha
        holder.txtSaldo.text = "S/. ${item.saldo}"  // Mostramos el saldo
        holder.txtSaldoContable.text = "S/. ${item.saldoContable}"  // Mostramos el saldo contable
        holder.txtUsuario.text = item.usuario  // Mostramos el nombre del usuario

        // Si también quieres mostrar una descripción, puedes usar item.descripcion si es necesario
        // holder.txtDescripcion.text = item.descripcion
    }


    override fun getItemCount() = lista.size
}
