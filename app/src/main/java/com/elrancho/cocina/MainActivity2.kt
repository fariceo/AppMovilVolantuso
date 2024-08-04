package com.elrancho.cocina

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elrancho.cocina.adapter.SuperHeroAdapter

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

       SuperHeroProvider.SuperHeroList

        initRecyclerView()
    }

    fun initRecyclerView(){


        val recyclerView=findViewById<RecyclerView>(R.id.recyclerSuperHero)
        recyclerView.layoutManager=LinearLayoutManager(this)
        recyclerView.adapter=SuperHeroAdapter(SuperHeroProvider.SuperHeroList)
    }
}