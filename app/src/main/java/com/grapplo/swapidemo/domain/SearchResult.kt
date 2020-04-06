package com.grapplo.swapidemo.domain

sealed class SearchResult {
    abstract val name: String
    abstract val size: Int?
    abstract val sizeUnit: String

    data class PlanetResult(val planet: Planet) : SearchResult() {
        override val name: String
            get() = planet.name
        override val size: Int?
            get() = planet.diameter.toIntOrNull()
        override val sizeUnit: String
            get() = "km"
    }

    data class PersonResult(val person: Person) : SearchResult() {
        override val name: String
            get() = person.name
        override val size: Int?
            get() = person.height.toIntOrNull()
        override val sizeUnit: String
            get() = "cm"
    }
}