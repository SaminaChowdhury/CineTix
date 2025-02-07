package com.example.cinetix

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.cinetix.Adapter.FilmListAdapter
import com.example.cinetix.Adapter.SliderAdapter
import com.example.cinetix.Models.Film
import com.example.cinetix.Models.SliderItems
import com.example.cinetix.databinding.ActivityMainBinding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ismaeldivita.chipnavigation.ChipNavigationBar


class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private val sliderHandler = Handler(Looper.getMainLooper())
    private val sliderInterval: Long = 3000 // Time interval in milliseconds
    private lateinit var database: FirebaseDatabase
    private lateinit var filmListAdapter: FilmListAdapter
    private val filteredMoviesList = ArrayList<Film>()

    private val sliderRunnable = Runnable {
        val currentItem = binding.viewPager2.currentItem
        val itemCount = binding.viewPager2.adapter?.itemCount ?: 0
        binding.viewPager2.currentItem = (currentItem + 1) % itemCount // Loop back to the first item
    }


    private val topMoviesList = ArrayList<Film>()
    private val upcomingMoviesList = ArrayList<Film>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        auth = FirebaseAuth.getInstance()


        binding.iconSettings.setOnClickListener {
            showSignOutConfirmation()
        }

        if (checkGooglePlayServices()) {

        } else {

            Toast.makeText(this, "Google Play Services not available", Toast.LENGTH_SHORT).show()
        }


        database = FirebaseDatabase.getInstance()


        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN


        initBanner()
        initTopMoving()
        initUpcoming()




        val seeAllTopMoviesTextView = findViewById<TextView>(R.id.button_see_all)
        seeAllTopMoviesTextView.setOnClickListener {
            val intent = Intent(this, SeeAllActivity::class.java)
            intent.putExtra("movies", topMoviesList)
            startActivity(intent)
        }


        val seeAllUpcomingMoviesTextView = findViewById<TextView>(R.id.button_see_all2)
        seeAllUpcomingMoviesTextView.setOnClickListener {
            val intent = Intent(this, SeeAllActivity::class.java)
            intent.putExtra("movies", upcomingMoviesList)
            startActivity(intent)
        }

        val chipNavigationBar = findViewById<ChipNavigationBar>(R.id.chipNavigationBar)
        chipNavigationBar.setOnItemSelectedListener { itemId ->
            when (itemId) {
                R.id.home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                }
                R.id.favorite -> {
                    startActivity(Intent(this, FavoriteActivity::class.java))
                }
                R.id.profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                }
            }
        }
    }

    private fun checkGooglePlayServices(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this)
        return resultCode == ConnectionResult.SUCCESS
    }

    private fun loadMovies() {
        val myRef: DatabaseReference = database.getReference("Items")
        binding.progressBarTopMovies.visibility = View.VISIBLE

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    topMoviesList.clear()
                    for (issue in snapshot.children) {
                        issue.getValue(Film::class.java)?.let { topMoviesList.add(it) }
                    }
                    filterMovies("")
                    binding.progressBarTopMovies.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        })
    }

    private fun filterMovies(query: String) {
        filteredMoviesList.clear()
        if (query.isEmpty()) {
            filteredMoviesList.addAll(topMoviesList)
        } else {
            for (movie in topMoviesList) {
                if (movie.Title?.contains(query, ignoreCase = true) == true) {
                    filteredMoviesList.add(movie)
                }
            }
        }
        filmListAdapter.notifyDataSetChanged()
    }


    private fun initTopMoving() {
        val myRef: DatabaseReference = database.getReference("Items")
        binding.progressBarTopMovies.visibility = View.VISIBLE

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (issue in snapshot.children) {
                        issue.getValue(Film::class.java)?.let { topMoviesList.add(it) }
                    }
                    if (topMoviesList.isNotEmpty()) {
                        binding.recyclerViewTopMovies.layoutManager =
                            LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
                        binding.recyclerViewTopMovies.adapter = FilmListAdapter(topMoviesList)

                        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.recycler_item_spacing)
                        binding.recyclerViewTopMovies.addItemDecoration(SpacingItemDecoration(spacingInPixels))
                    }
                    binding.progressBarTopMovies.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        })
    }

    private fun initUpcoming() {
        val myRef: DatabaseReference = database.getReference("Items")
        binding.progressBarUpcoming.visibility = View.VISIBLE

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (issue in snapshot.children) {
                        issue.getValue(Film::class.java)?.let { upcomingMoviesList.add(it) }
                    }
                    if (upcomingMoviesList.isNotEmpty()) {
                        binding.recyclerViewUpcoming.layoutManager =
                            LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
                        binding.recyclerViewUpcoming.adapter = FilmListAdapter(upcomingMoviesList)

                        // Add spacing decoration
                        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.recycler_item_spacing)
                        binding.recyclerViewUpcoming.addItemDecoration(SpacingItemDecoration(spacingInPixels))
                    }
                    binding.progressBarUpcoming.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        })
    }

    private fun showSignOutConfirmation() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Sign Out")
        builder.setMessage("Are you sure you want to sign out?")
        builder.setPositiveButton("Yes") { _, _ ->
            signOutUser()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun signOutUser() {
        auth.signOut()
        Toast.makeText(this, "Signed out successfully!", Toast.LENGTH_SHORT).show()


        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear back stack
        startActivity(intent)
        finish()
    }

    private fun initBanner() {
        val banners = mutableListOf(
            SliderItems("https://res.cloudinary.com/dpdc32aax/image/upload/v1736527468/wide_a3ehr4.jpg"),
            SliderItems("https://res.cloudinary.com/dpdc32aax/image/upload/v1736527467/wide1_u7xjzf.jpg"),
            SliderItems("https://res.cloudinary.com/dpdc32aax/image/upload/v1736527468/wide3_klbvyk.jpg")
        )
        setupSlider(banners)
    }

    private fun setupSlider(items: MutableList<SliderItems>) {
        binding.viewPager2.adapter = SliderAdapter(items, binding.viewPager2)
        binding.viewPager2.clipToPadding = false
        binding.viewPager2.clipChildren = false
        binding.viewPager2.offscreenPageLimit = 3
        binding.viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val transformer = CompositePageTransformer().apply {
            addTransformer(MarginPageTransformer(40))
            addTransformer { page, position ->
                val scale = 0.85f + (1 - Math.abs(position)) * 0.15f
                page.scaleY = scale
            }
        }
        binding.viewPager2.setPageTransformer(transformer)


        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, sliderInterval)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        sliderHandler.postDelayed(sliderRunnable, sliderInterval)
    }

    override fun onPause() {
        super.onPause()
        sliderHandler.removeCallbacks(sliderRunnable)
    }
}