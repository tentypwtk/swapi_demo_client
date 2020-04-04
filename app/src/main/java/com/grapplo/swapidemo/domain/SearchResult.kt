package com.grapplo.swapidemo.domain

sealed class SearchResult {
    abstract val name: String
    abstract val size: Double

    data class PlanetResult(val planet: Planet) : SearchResult() {
        override val name: String
            get() = planet.name
        override val size: Double
            get() = planet.diameter.toDouble()
    }

    data class PersonResult(val person: Person) : SearchResult() {
        override val name: String
            get() = person.name
        override val size: Double
            get() = person.height.toDouble()
    }
}