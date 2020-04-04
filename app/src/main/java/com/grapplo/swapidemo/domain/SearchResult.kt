package com.grapplo.swapidemo.domain

import androidx.core.text.isDigitsOnly

sealed class SearchResult {
    abstract val name: String
    abstract val size: Int?

    data class PlanetResult(val planet: Planet) : SearchResult() {
        override val name: String
            get() = planet.name
        override val size: Int?
            get() = planet.diameter.toIntOrNull()
    }

    data class PersonResult(val person: Person) : SearchResult() {
        override val name: String
            get() = person.name
        override val size: Int?
            get() = person.height.toIntOrNull()
    }
}