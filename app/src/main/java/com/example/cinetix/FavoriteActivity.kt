package com.example.cinetix

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cinetix.Adapter.FilmListAdapter
import com.example.cinetix.Models.Film
import com.google.firebase.firestore.FirebaseFirestore

class FavoriteActivity : AppCompatActivity() {

    private lateinit var filmRecyclerView: RecyclerView
    private lateinit var filmListAdapter: FilmListAdapter
    private var filmList: MutableList<Film> = mutableListOf()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        filmRecyclerView = findViewById(R.id.recycler_view_favorites)
        filmListAdapter = FilmListAdapter(filmList)

        filmRecyclerView.layoutManager = LinearLayoutManager(this)
        filmRecyclerView.adapter = filmListAdapter

        loadBookmarkedMovies()
    }

    private fun loadBookmarkedMovies() {
        db.collection("favorites")
            .get()
            .addOnSuccessListener { documents ->
                filmList.clear() // Clear the list to avoid duplicates
                for (document in documents) {
                    val id = document.getString("id")
                    val title = document.getString("name")
                    val description = document.getString("description")
                    val posterUrl = document.getString("posterUrl")

                    if (!id.isNullOrEmpty() && !title.isNullOrEmpty()) {
                        val film = Film(
                            id = id,
                            Title = title,
                            Description = description,
                            Poster = posterUrl
                        )
                        filmList.add(film)
                    }
                }
                filmListAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load favorites!", Toast.LENGTH_SHORT).show()
            }
    }
}