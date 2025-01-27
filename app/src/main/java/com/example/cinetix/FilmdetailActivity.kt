package com.example.cinetix.Activity

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.cinetix.Adapter.CastListAdapter
import com.example.cinetix.Adapter.CategoryEachFilmAdapter
import com.example.cinetix.Models.Film
import com.example.cinetix.databinding.ActivityFilmdetailBinding
import eightbitlab.com.blurview.RenderScriptBlur

class FilmdetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFilmdetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilmdetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Full-screen layout
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setVariable()
    }

    private fun setVariable() {
        try {
            val item: Film = intent.getParcelableExtra("object")!!
            Log.d("FilmdetailActivity", "Film object received: $item")

            val requestOptions = RequestOptions().transform(CenterCrop(), GranularRoundedCorners(0f, 0f, 50f, 50f))

            Glide.with(this)
                .load(item.Poster)
                .apply(requestOptions)
                .into(binding.filmPic)
            Log.d("FilmdetailActivity", "Poster loaded")

            binding.titleTxt.text = item.Title
            binding.movieTimeTxt.text = "${item.Year} - ${item.Time}"
            binding.imdbTxt.text = "IMDB ${item.Imdb}"
            binding.movieSummaryTxt.text = item.Description
            Log.d("FilmdetailActivity", "Text fields set")

            binding.backBtn.setOnClickListener {
                finish()
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
            Log.d("FilmdetailActivity", "Blur view set up")

            item.Genre?.let {
                binding.genreView.adapter = CategoryEachFilmAdapter(it)
                binding.genreView.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                Log.d("FilmdetailActivity", "Genre view set up")
            }

            item.Casts?.let {
                binding.castListView.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                binding.castListView.adapter = CastListAdapter(it)
                Log.d("FilmdetailActivity", "Cast list view set up")
            }
        } catch (e: Exception) {
            Log.e("FilmdetailActivity", "Error in setVariable", e)
        }
    }
}