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

            val pAdd = 0.2
            val pRemove = 0.2
            val pSwap = 0.2

            var add = random.nextDouble() < pAdd
            var remove = random.nextDouble() < pRemove
            var swap = random.nextDouble() < pSwap

            while (add || remove || swap) {
                if (add) {
                    val rInd = random.nextInt(1, genes.size - 1)
                    val gInd = random.nextInt(0, genesPool.size - 1)

                    genes.add(rInd, genesPool[gInd])
                    genesPool.removeAt(gInd)
                }
                if (remove) {
                    val rInd = random.nextInt(1, genes.size - 1)

                    genesPool.add(genes[rInd])
                    genes.remove(rInd)
                }
                if (swap) {
                    val lind = random.nextInt(1, genes.size - 1)
                    val rind = random.nextInt(1, genes.size - 1)

                    if (lind != rind) {
                        val lval = genes[lind]
                        val rval = genes[rind]

                        genes[rind] = lval
                        genes[lind] = rval
                    }
                }

                add = random.nextDouble() < pAdd
                remove = random.nextDouble() < pRemove
                swap = random.nextDouble() < pSwap
            }

            Genotype(genes)
        }
        genetic.setSelectToCrossover { genotypes ->
            val selected = arrayListOf<Pair<Genotype<Int, Double>, Genotype<Int, Double>>>()

            while (genotypes.size != 256) {
                val l = random.nextInt(genotypes.size)
                val r = random.nextInt(genotypes.size)

                if (l != r) {
                    selected.add(Pair(genotypes[l], genotypes[r]))
                }
            }

            selected
        }
        genetic.setSelectToMutation { genotypes ->
            val selected = arrayListOf<Genotype<Int, Double>>()

            (0..genotypes.size).forEach { i ->
                if (random.nextDouble() < 0.20) {
                    selected.add(genotypes[i])
                }
            }

            selected
        }
        genetic.setSelectToSelection { genotypes ->
            val selected = arrayListOf<Genotype<Int, Double>>()

            (0..genotypes.size).forEach { _ ->
                selected.add(randomBy(genotypes) { it.fitness!! })
            }

            selected
        }

        genetic.run(512, 2048) {
            val genes = arrayListOf<Int>()
            val genesPool = arrayListOf<Int>()

            (1 until points.size - 1).forEach {
                genesPool.add(it)
            }

            while (genesPool.isNotEmpty() && random.nextDouble() < 0.6) {
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