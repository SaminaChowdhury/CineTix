package com.example.cinetix

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.cinetix.Adapter.CastListAdapter
import com.example.cinetix.Adapter.CategoryEachFilmAdapter
import com.example.cinetix.Models.Film
import com.example.cinetix.databinding.ActivityFilmdetailBinding
import com.google.firebase.firestore.FirebaseFirestore
import eightbitlab.com.blurview.RenderScriptBlur

class FilmdetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFilmdetailBinding
    private lateinit var currentFilm: Film
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilmdetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setVariable()
    }

    private fun setVariable() {
        currentFilm = intent.getParcelableExtra("object")!!

        val requestOptions = RequestOptions().transform(CenterCrop(), GranularRoundedCorners(0f, 0f, 50f, 50f))

        Glide.with(this)
            .load(currentFilm.Poster)
            .apply(requestOptions)
            .into(binding.filmPic)

        binding.titleTxt.text = currentFilm.Title
        binding.movieTimeTxt.text = "${currentFilm.Year} - ${currentFilm.Time}"
        binding.imdbTxt.text = "IMDB ${currentFilm.Imdb}"
        binding.movieSummaryTxt.text = currentFilm.Description

        binding.backBtn.setOnClickListener {
            finish()
        }
        binding.buyTicketBtn.setOnClickListener {
            val intent = Intent(this, SeatListActivity::class.java)
            intent.putExtra("film", currentFilm)
            startActivity(intent)
        }

        binding.bookmarkBtn.setOnClickListener {
            bookmarkMovie(currentFilm)
        }
        binding.share.setOnClickListener {
            val shareText = "Check out this movie: ${currentFilm.Title}\nGenre: ${currentFilm.Genre}\nIMDB: ${currentFilm.Imdb}"
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }


        val radius = 10f
        val decorView = window.decorView
        val rootView = decorView.findViewById<ViewGroup>(android.R.id.content)
        val windowsBackground = decorView.background

        binding.blurView.setupWith(rootView, RenderScriptBlur(this))
            .setFrameClearDrawable(windowsBackground)
            .setBlurRadius(radius)
        binding.blurView.outlineProvider = ViewOutlineProvider.BACKGROUND
        binding.blurView.clipToOutline = true

        currentFilm.Genre?.let {
            binding.genreView.adapter = CategoryEachFilmAdapter(it)
            binding.genreView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        }
        currentFilm.Casts?.let {
            val gridLayoutManager = GridLayoutManager(this, 4) // 2 columns
            binding.castListView.layoutManager = gridLayoutManager
            binding.castListView.addItemDecoration(SpacingItemDecoration2(4, 3)) // Adjusted spacing
            binding.castListView.adapter = CastListAdapter(it)
        }
    }

    private fun bookmarkMovie(film: Film) {
        val movieData = hashMapOf(
            "name" to film.Title,
            "id" to film.id,
            "description" to film.Description,
            "posterUrl" to film.Poster
        )

        db.collection("favorites")
            .add(movieData)
            .addOnSuccessListener {
                Toast.makeText(this, "Movie saved!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save!", Toast.LENGTH_SHORT).show()
            }
    }
}