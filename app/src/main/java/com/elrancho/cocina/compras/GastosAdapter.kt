package com.elrancho.cocina.compras



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.elrancho.cocina.R



class GastosAdapter(private val listaGastos: List<Gasto>) :
    RecyclerView.Adapter<GastosAdapter.GastoViewHolder>() {

    class GastoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtProducto: TextView = view.findViewById(R.id.txtProducto)
        val txtPrecio: TextView = view.findViewById(R.id.txtPrecio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GastoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gasto, parent, false)
        return GastoViewHolder(view)
    }

    override fun onBindViewHolder(holder: GastoViewHolder, position: Int) {
        val gasto = listaGastos[position]
        holder.txtProducto.text = gasto.producto
        holder.txtPrecio.text = "S/. ${gasto.precio}"
    }

    override fun getItemCount(): Int = listaGastos.size
}
