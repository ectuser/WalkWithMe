package com.example.walkwithme.algorithms.genetic

class Generation<T, V>(
    private val fitness: (Genotype<T, V>) -> Genotype<T, V>,
    private val bestOf: (Genotype<T, V>, Genotype<T, V>) -> Genotype<T, V>,
    private val crossover: (Genotype<T, V>, Genotype<T, V>) -> Genotype<T, V>,
    private val mutate: (Genotype<T, V>) -> Genotype<T, V>,
    private val selectToCrossover: (List<Genotype<T, V>>) -> List<Pair<Genotype<T, V>, Genotype<T, V>>>,
    private val selectToMutation: (List<Genotype<T, V>>) -> List<Genotype<T, V>>,
    private val selectToSelection: (List<Genotype<T, V>>) -> List<Genotype<T, V>>
) {
    private var _genotypes = arrayListOf<Genotype<T, V>>()
    private var _best = Genotype<T, V>(arrayListOf())

    val genotypes: ArrayList<Genotype<T, V>>
        get() = _genotypes

    val best: Genotype<T, V>
        get() = _best

    fun generate(
        size: Int,
        generator: () -> Genotype<T, V>
    ) {
        _genotypes.clear()

        (0..size).forEach { _ ->
            val genotype = fitness(generator())

            _genotypes.add(genotype)
            _best = bestOf(_best, genotype)
        }
    }

    fun update() {
        crossoverStage()
        mutationStage()
        selectionStage()
    }

    private fun crossoverStage() {
        selectToCrossover(_genotypes).forEach {
            val a = it.first
            val b = it.second
            val genotype = fitness(crossover(a, b))

            _genotypes.add(genotype)
            _best = bestOf(_best, genotype)
        }
    }

    private fun mutationStage() {
        selectToMutation(_genotypes).forEach {
            val ind = _genotypes.indexOf(it)
            val genotype = fitness(mutate(it))

            _genotypes[ind] = genotype
            _best = bestOf(_best, genotype)
        }
    }

    private fun selectionStage() {
        _genotypes = ArrayList(selectToSelection(_genotypes))
    }
}