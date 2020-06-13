package com.example.walkwithme.algorithms.genetic

class Generation<T, V>(
    private val size:                Int,
    private val generateGenotype:   ()                                  -> Genotype<T, V>,
    private val fitness:            (Genotype<T, V>)                    -> V,
    private val crossover:          (Genotype<T, V>, Genotype<T, V>)    -> Genotype<T, V>,
    private val mutate:             (Genotype<T, V>)                    -> Genotype<T, V>,
    private val selectToCrossover:  (List<V>)                           -> List<Pair<Int, Int>>,
    private val selectToMutation:   (List<V>)                           -> List<Int>,
    private val selectToSelection:  (List<V>)                           -> List<Int>
) {
    private val genotypes: ArrayList<Genotype<T, V>> = arrayListOf()

    init {
        (0..size).forEach {
            genotypes.add(generateGenotype())
        }
    }

    fun update() {
        val fitnessValues: ArrayList<V> = arrayListOf()

        genotypes.forEach {
            fitnessValues.add(fitness(it))
        }

        crossoverStage(fitnessValues)
        mutationStage(fitnessValues)
        selectionStage(fitnessValues)
    }

    private fun crossoverStage(fitnessValues: ArrayList<V>) {
        selectToCrossover(fitnessValues).forEach {
            val i = it.first
            val j = it.second
            val genotype = crossover(genotypes[i], genotypes[j])

            genotypes.add(genotype)
            fitnessValues.add(fitness(genotype))
        }
    }

    private fun mutationStage(fitnessValues: ArrayList<V>) {
        selectToMutation(fitnessValues).forEach {
            val genotype = mutate(genotypes[it])

            genotypes[it] = genotype
            fitnessValues[it] = fitness(genotype)
        }
    }

    private fun selectionStage(fitnessValues: ArrayList<V>) {
        val toRemoveItem: ArrayList<Genotype<T, V>> = arrayListOf()
        val toRemoveIndex: ArrayList<Int> = arrayListOf()
        for (i in 0 until genotypes.size) {
            toRemoveIndex.add(i)
        }

        toRemoveIndex.removeAll(selectToSelection(fitnessValues))
        toRemoveIndex.forEach {
            toRemoveItem.add(genotypes[it])
        }

        genotypes.removeAll(toRemoveItem)
    }
}