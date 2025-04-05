package com.elrancho.cocina.ordenes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.elrancho.cocina.R
import com.elrancho.cocina.ordenes.UsuarioSaldo

// Adaptador para el RecyclerView que muestra los usuarios
class UsuarioAdapter(private val usuarios: List<UsuarioSaldo>, private val onItemClick: (UsuarioSaldo) -> Unit) :
    RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {

    // Método que crea un ViewHolder a partir de la vista correspondiente
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_usuario, parent, false)
        return UsuarioViewHolder(view)
    }

    // Método que asigna los datos de cada usuario a su vista correspondiente
    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = usuarios[position]
        holder.bind(usuario)
    }

    // Método que retorna el tamaño de la lista de usuarios
    override fun getItemCount(): Int {
        return usuarios.size
    }

    // ViewHolder que maneja la vista para cada usuario
    inner class UsuarioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nombreUsuario: TextView = view.findViewById(R.id.textNombreUsuario)
        private val saldoUsuario: TextView = view.findViewById(R.id.textSaldoUsuario)

        // Método que asigna los datos de un usuario a los TextViews
        fun bind(usuario: UsuarioSaldo) {
            nombreUsuario.text = usuario.nombre
            saldoUsuario.text = "Saldo: $${usuario.saldo}"
            itemView.setOnClickListener {
                // Al hacer clic en un usuario, ejecutamos la función onItemClick con el usuario seleccionado
                onItemClick(usuario)
            }
        }
    }
}
