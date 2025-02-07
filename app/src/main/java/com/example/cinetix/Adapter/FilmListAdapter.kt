// FilmListAdapter.kt
package com.example.cinetix.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.cinetix.FilmdetailActivity
import com.example.cinetix.Models.Film
import com.example.cinetix.databinding.ViewholderFilmBinding

class FilmListAdapter(private var items: MutableList<Film>) :
    RecyclerView.Adapter<FilmListAdapter.ViewHolder>() {

    private var context: Context? = null

    inner class ViewHolder(private val binding: ViewholderFilmBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(film: Film) {
            binding.nameText.text = film.Title
            val requestOptions = RequestOptions()
                .transform(CenterCrop(), RoundedCorners(30))

            context?.let {
                Glide.with(it)
                    .load(film.Poster)
                    .apply(requestOptions)
                    .dontAnimate()
                    .into(binding.pic)

                binding.root.setOnClickListener {
                    val intent = Intent(context, FilmdetailActivity::class.java)
                    intent.putExtra("object", film)
                    context!!.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding = ViewholderFilmBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Film>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}