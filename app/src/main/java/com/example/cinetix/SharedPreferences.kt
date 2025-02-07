package com.example.cinetix

import android.content.Context
import android.content.SharedPreferences
import com.example.cinetix.Models.Film
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SharedPreferencesUtil {
    private const val PREFS_NAME = "cinetix_prefs"
    private const val BOOKMARKED_MOVIES_KEY = "bookmarked_movies"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveBookmarkedMovies(context: Context, movies: List<Film>) {
        val gson = Gson()
        val json = gson.toJson(movies)
        getSharedPreferences(context).edit().putString(BOOKMARKED_MOVIES_KEY, json).apply()
    }

    fun getBookmarkedMovies(context: Context): List<Film> {
        val gson = Gson()
        val json = getSharedPreferences(context).getString(BOOKMARKED_MOVIES_KEY, null)


        val type = object : TypeToken<List<Film>>() {}.type
        return if (json != null) {
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }
}