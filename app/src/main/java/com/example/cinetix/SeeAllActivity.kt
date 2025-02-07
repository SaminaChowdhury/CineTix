package com.example.cinetix

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cinetix.Adapter.FilmListAdapter
import com.example.cinetix.Models.Film

class SeeAllActivity : AppCompatActivity() {

    private lateinit var filmRecyclerView: RecyclerView
    private lateinit var filmListAdapter: FilmListAdapter
    private var filmList: MutableList<Film> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_all)

        filmRecyclerView = findViewById(R.id.recycler_view_see_all)
        filmListAdapter = FilmListAdapter(filmList)

        filmRecyclerView.layoutManager = LinearLayoutManager(this)
        filmRecyclerView.adapter = filmListAdapter


        val movies = intent.getParcelableArrayListExtra<Film>("movies")
        if (movies != null) {
            filmList.addAll(movies)
            filmListAdapter.notifyDataSetChanged()
        }
    }
}