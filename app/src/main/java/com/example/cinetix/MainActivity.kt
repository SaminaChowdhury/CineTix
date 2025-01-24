package com.example.cinetix

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.cinetix.Adapter.SliderAdapter
import com.example.cinetix.Models.SliderItems
import com.example.cinetix.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val sliderHandler = Handler(Looper.getMainLooper())
    private val sliderInterval: Long = 3000 // Time interval in milliseconds

    private val sliderRunnable = Runnable {
        val currentItem = binding.viewPager2.currentItem
        val itemCount = binding.viewPager2.adapter?.itemCount ?: 0
        binding.viewPager2.currentItem = (currentItem + 1) % itemCount // Loop back to the first item
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Full-screen layout
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        // Initialize the banner slider
        initBanner()
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

        // Auto-scroll with lifecycle management
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
