package com.example.cinetix.Models

import java.io.Serializable

data class Film2(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String
) : Serializable