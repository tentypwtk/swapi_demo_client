package com.grapplo.swapidemo.domain

import kotlinx.android.parcel.Parcelize

@Parcelize
data class Planet(
    override val name: String,
    val rotation_period: String,
    val orbital_period: String,
    val diameter: String,
    val climate: String,
    val gravity: String,
    val terrain: String,
    val surface_water: String,
    val population: String,
    val residents: List<String>,
    val films: List<String>,
    val created: String,
    val edited: String,
    val url: String
) : SwEntity {
    override val description
        get() = "Rotation period\t:\t" + rotation_period + "\n" +
                "Orbital period\t:\t" + orbital_period + "\n" +
                "Diameter\t:\t" + diameter + "\n" +
                "Climate\t:\t" + climate + "\n" +
                "Gravity\t:\t" + gravity + "\n" +
                "Terrain\t:\t" + terrain + "\n" +
                "Surface water\t:\t" + surface_water + "\n" +
                "Population\t:\t" + population + "\n" +
                "Residents count\t:\t" + residents.count() + "\n" +
                "Films count:\t:\t" + films.count() + "\n" +
                "Created\t:\t" + created + "\n" +
                "Edited\t:\t" + edited
}