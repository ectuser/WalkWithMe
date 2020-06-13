package com.example.walkwithme.algorithms.genetic

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.random.Random

class Example {
    private val len = 6
    private val random = Random(0)
    private val points = listOf(
        listOf(+0.0, 0.0, 1.0),
        listOf(-1.0, 1.0, 1.0),
        listOf(-1.0, 2.0, 1.0),
        listOf(-1.0, 3.0, 1.0),
        listOf(+0.0, 2.0, 1.0),
        listOf(-1.0, 1.5, 1.0),
        listOf(-1.0, 2.5, 1.0),
        listOf(+0.0, 4.0, 1.0)
    )

    private fun<T> randomBy(ts: List<T>, p: (T) -> Double): T {
        var sum = random.nextDouble(ts.map(p).sum())
        var i = -1

        while (sum > 0) {
            ++i
            sum -= p(ts[i])
        }

        return ts[i]
    }

    private fun distance(a: List<Double>, b: List<Double>): Double {
        return sqrt((a[0] - b[0]) * (a[0] - b[0]) + (a[1] - b[1]) * (a[1] - b[1]))
    }

    fun run() {
        val genetic = Genetic<Int, Double>()

        genetic.setFitness { genotype ->
            if (genotype.fitness != null) {
                return@setFitness genotype
            }

            var l = 0.0
            (0 until genotype.genes.size - 1).forEach {
                val a = genotype.genes[it + 0]
                val b = genotype.genes[it + 1]

                l += distance(points[a], points[b])
            }

            val w = points.sumByDouble { it.last() }
            val fitness = w / abs(l - len)
            Genotype(genotype.genes, fitness)
        }

        genetic.setBestOf { genotypeA, genotypeB ->
            if (genotypeA.fitness!! >= genotypeB.fitness!!) genotypeA else genotypeB
        }

        genetic.setCrossover { genotypeA, genotypeB ->
            val fa = genotypeA.fitness!!
            val fb = genotypeB.fitness!!
            val wa = fa / (fa + fb)
            val wb = fb / (fa + fb)

            val weightedGenes = hashMapOf<Int, Double>()
            genotypeA.genes.forEachIndexed { i, gene -> weightedGenes += mapOf(gene to wa * i) }
            genotypeB.genes.forEachIndexed { i, gene -> weightedGenes += mapOf(gene to wb * i) }

            val minBound = min(genotypeA.genes.size, genotypeB.genes.size)
            val maxBound = max(genotypeA.genes.size, genotypeB.genes.size)
            val size = random.nextInt(minBound, maxBound + 1)
            val genes = arrayListOf<Int>()

            (0..size).forEach { _ ->
                val gene = weightedGenes.minBy { it.value }!!

                genes.add(gene.key)
                weightedGenes.remove(gene.key)
            }

            if (!genes.contains(genotypeA.genes.first())) { genes.add(0, genotypeA.genes.first()) }
            if (!genes.contains(genotypeA.genes.last())) { genes.add(genes.size, genotypeA.genes.last()) }

            Genotype(genes)
        }

        genetic.setMutate { genotype ->
            val genes = arrayListOf<Int>()
            val genesPool = arrayListOf<Int>()

            (genotype.genes.first()..genotype.genes.last()).forEach { i ->
                genesPool.add(i)
            }

            for (i in genotype.genes.indices) {
                genes.add(genotype.genes[i])
                genesPool.remove(genotype.genes[i])
            }

            val pAdd = 0.25
            val pRemove = 0.25
            val pSwap = 0.25

            var add = random.nextDouble() < pAdd && genesPool.isNotEmpty()
            var remove = random.nextDouble() < pRemove && genes.size > 2
            var swap = random.nextDouble() < pSwap && genes.size > 3

            while (add || remove || swap) {
                if (add) {
                    val genesInd = random.nextInt(1, genes.size - 1)
                    val poolInd = random.nextInt(0, genesPool.size - 1)

                    genes.add(genesInd, genesPool[poolInd])
                    genesPool.removeAt(poolInd)
                }
                if (remove) {
                    val genesInd = random.nextInt(1, genes.size - 1)

                    genesPool.add(genes[genesInd])
                    genes.remove(genesInd)
                }
                if (swap) {
                    val li = random.nextInt(1, genes.size - 1)
                    val ri = random.nextInt(1, genes.size - 1)

                    if (li != ri) {
                        val lv = genes[li]
                        val rv = genes[ri]

                        genes[ri] = lv
                        genes[li] = rv
                    }
                }

                add = random.nextDouble() < pAdd && genesPool.isNotEmpty()
                remove = random.nextDouble() < pRemove && genes.size > 2
                swap = random.nextDouble() < pSwap && genes.size > 3
            }

            Genotype(genes)
        }

        genetic.setSelectToCrossover { genotypes, generationSize ->
            val selected = arrayListOf<Pair<Genotype<Int, Double>, Genotype<Int, Double>>>()

            while (genotypes.size < generationSize / 2) {
                val l = random.nextInt(genotypes.size)
                val r = random.nextInt(genotypes.size)
                if (l != r &&
                    !selected.contains(Pair(genotypes[l], genotypes[r]))
                ) {
                    selected.add(Pair(genotypes[l], genotypes[r]))
                }
            }

            selected
        }

        genetic.setSelectToMutation { genotypes, generationSize ->
            val selected = arrayListOf<Genotype<Int, Double>>()

            while (selected.size < generationSize / 4) {
                val i = random.nextInt(genotypes.size)
                if (!selected.contains(genotypes[i])) {
                    selected.add(genotypes[i])
                }
            }

            selected
        }

        genetic.setSelectToSelection { genotypes, generationSize ->
            val selected = arrayListOf<Genotype<Int, Double>>()

            while (selected.size < generationSize) {
                val genotype = randomBy(genotypes) { it.fitness!! }
                if (!selected.contains(genotype)) {
                    selected.add(genotype)
                }
            }

            selected
        }

        genetic.run(512, 2048) {
            val genes = arrayListOf<Int>()
            val genesPool = arrayListOf<Int>()
            val probability = 1 - 2 / (points.size - 2)

            (1 until points.size - 1).forEach {
                genesPool.add(it)
            }

            while (genesPool.isNotEmpty() && random.nextDouble() < probability) {
                val point = genesPool.random()

                genesPool.remove(point)
                genes.add(point)
            }

            genes.add(0, 0)
            genes.add(genes.size, points.size - 1)

            Genotype(genes)
        }
    }
}