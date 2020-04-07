package com.grapplo.swapidemo.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Person(
    override val name: String,
    val height: String,
    val mass: String,
    val hair_color: String,
    val skin_color: String,
    val eye_color: String,
    val birth_year: String,
    val gender: String,
    val homeworld: String,
    val films: List<String>,
    val species: List<String>,
    val vehicles: List<String>,
    val starships: List<String>,
    val url: String
) : SwEntity {
    override val description
        get() = "Height\t:\t" + height + "\n" +
                "Mass\t:\t" + mass + "\n" +
                "Hair color\t:\t" + hair_color + "\n" +
                "Skin color\t:\t" + skin_color + "\n" +
                "Eye color\t:\t" + eye_color + "\n" +
                "Birth year\t:\t" + birth_year + "\n" +
                "Gender\t:\t" + gender + "\n" +
                "Home world\t:\t" + homeworld + "\n" +
                "Films count\t:\t" + films.count() + "\n" +
                "Species count\t:\t" + species.count() + "\n" +
                "Vehicles count:\t:\t" + vehicles.count() + "\n" +
                "Starships count:\t:\t" + starships.count()
}