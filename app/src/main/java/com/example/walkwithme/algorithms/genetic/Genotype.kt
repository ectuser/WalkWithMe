package com.example.walkwithme.algorithms.genetic

data class Genotype<T, V>(
    val genes: List<T>,
    val fitness: V? = null
)