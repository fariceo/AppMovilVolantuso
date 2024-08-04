package com.elrancho.cocina.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.elrancho.cocina.R
import com.elrancho.cocina.SuperHero

class SuperHeroAdapter(private val SuperHeroList:List<SuperHero>):RecyclerView.Adapter<SuperHeroViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuperHeroViewHolder {
val layoutInflater=LayoutInflater.from(parent.context)

        return SuperHeroViewHolder(layoutInflater.inflate(R.layout.item_superhero,parent,false))
    }

    override fun getItemCount(): Int = SuperHeroList.size


    override fun onBindViewHolder(holder: SuperHeroViewHolder, position: Int) {
        val item=SuperHeroList[position]
        holder.render(item)

    }
}