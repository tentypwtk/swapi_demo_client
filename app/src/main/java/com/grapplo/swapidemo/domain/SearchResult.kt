package com.grapplo.swapidemo.domain

sealed class SearchResult<T> {
    abstract val item: T
    abstract val name: String
    abstract val size: Int?
    abstract val sizeUnit: String

    data class PlanetResult(val planet: Planet) : SearchResult<Planet>() {
        override val item: Planet
            get() = planet
        override val name: String
            get() = planet.name
        override val size: Int?
            get() = planet.diameter.toIntOrNull()
        override val sizeUnit: String
            get() = "km"
    }

    data class PersonResult(val person: Person) : SearchResult<Person>() {
        override val item: Person
            get() = person
        override val name: String
            get() = person.name
        override val size: Int?
            get() = person.height.toIntOrNull()
        override val sizeUnit: String
            get() = "cm"
    }
}