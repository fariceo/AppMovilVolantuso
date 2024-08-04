package com.elrancho.cocina.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.elrancho.cocina.R
import com.elrancho.cocina.SuperHero

class SuperHeroViewHolder(View:View):ViewHolder(View) {
    val superHero=View.findViewById<TextView>(R.id.tvSuperHeroName)
    val realName=View.findViewById<TextView>(R.id.tvRealName)
    val publisher=View.findViewById<TextView>(R.id.tvPubliser)
    val photo=View.findViewById<ImageView>(R.id.ivSuperHero)
    fun render(superHeroModel:SuperHero){
superHero.text=superHeroModel.superhero
        realName.text=superHeroModel.realname
        publisher.text=superHeroModel.publisher
Glide.with(photo.context).load(superHeroModel.photo).into(photo)
    }
}