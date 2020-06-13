package com.example.walkwithme.algorithms.genetic

data class Genotype<T, V>(
    val genes: ArrayList<T>,
    val fitness: V
)