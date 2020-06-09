package com.example.walkwithme.algorithms.genetic

class Generation<T, V>(
    generateGenotype:               (ArrayList<Genotype<T>>)    -> Genotype<T>,
    private val fitness:            (Genotype<T>)               -> V,
    private val crossover:          (Genotype<T>, Genotype<T>)  -> Genotype<T>,
    private val mutate:             (Genotype<T>)               -> Genotype<T>,
    private val selectToCrossover:  (ArrayList<V>)              -> ArrayList<Pair<Int, Int>>,
    private val selectToMutation:   (ArrayList<V>)              -> ArrayList<Int>,
    private val selectToSelection:  (ArrayList<V>)              -> ArrayList<Int>
) {
    private val genotypes: ArrayList<Genotype<T>> = arrayListOf()

    init {
        var genotype: Genotype<T>? = generateGenotype(genotypes)
        while (genotype != null) {
            genotypes.add(genotype)

            genotype = generateGenotype(genotypes)
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
        val toRemoveItem: ArrayList<Genotype<T>> = arrayListOf()
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