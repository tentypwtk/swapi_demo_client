package com.grapplo.swapidemo.domain

data class Person(
    val name: String,
    val height: String,
    val mass: String,
    val hair_color: String,
    val skin_color: String,
    val eye_color: String,
    val birth_year: String,
    val gender: String,
    val homeworld: String,
    val films: String,
    val species: String,
    val vehicles: List<String>,
    val starships: List<String>,
    val url: String
)