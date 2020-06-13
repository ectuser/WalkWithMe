package com.example.walkwithme.algorithms.genetic

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.random.Random

fun runGenetic() {

    val len = 6
    val random = Random(0)
    val points = listOf(
        listOf(+0.0, 0.0, 1.0),
        listOf(-1.0, 1.0, 1.0),
        listOf(-1.0, 2.0, 1.0),
        listOf(-1.0, 3.0, 1.0),
        listOf(+0.0, 2.0, 1.0),
        listOf(-1.0, 1.5, 1.0),
        listOf(-1.0, 2.5, 1.0),
        listOf(+0.0, 4.0, 1.0)
    )

    fun generate(p: List<Double>): Int {
        var a = random.nextDouble(p.sum())
        var i = -1

        while (a > 0) {
            ++i
            a -= p[i]
        }

        return i
    }

    fun distance(a: List<Double>, b: List<Double>): Double {
        return sqrt((a[0] - b[0]) * (a[0] - b[0]) + (a[1] - b[1]) * (a[1] - b[1]))
    }

    val generation = Generation<Int, Double>(
        128,
        {
            val result = arrayListOf<Int>()
            val genesPool = arrayListOf<Int>()

            for (i in 1 until points.size - 1) {
                genesPool.add(i)
            }

            while (genesPool.isNotEmpty() && random.nextDouble() < 0.6) {
                val point = genesPool.random()

                genesPool.remove(point)
                result.add(point)
            }

            result.add(0, 0)
            result.add(result.size, points.size - 1)

            Genotype(result, 0.0)
        },
        {
            var w = 0.0
            var l = 0.0

            for (i in 0 until it.genes.size) {
                w += points[i].last()
            }

            for (i in 0 until it.genes.size - 1) {
                val a = it.genes[i + 0]
                val b = it.genes[i + 1]

                l += distance(points[a], points[b])
            }

            w / abs(l - len)
        },
        { a, b ->
            val result = arrayListOf<Int>()

            val fa = a.fitness
            val fb = b.fitness
            val wa = fa / (fa + fb)
            val wb = fb / (fa + fb)

            var genes = arrayListOf<Int>()
            genes.addAll(a.genes)
            genes.addAll(b.genes)
            genes = ArrayList(genes.distinct())

            val set = arrayListOf<Pair<Int, Double>>()

            for (i in 0 until genes.size) {
                var ai = a.genes.indexOfFirst { it == genes[i] }
                var bi = b.genes.indexOfFirst { it == genes[i] }

                ai = if (ai == -1) 0 else ai
                bi = if (bi == -1) 0 else bi

                set.add(Pair(genes[i], wa * ai + wb * bi))
            }

            set.sortBy { it.second }

            val minBound = min(a.genes.size, b.genes.size)
            val maxBound = max(a.genes.size, b.genes.size)
            val size = random.nextInt(minBound, maxBound + 1)

            for (i in 0 until size) {
                result.add(set[i].first)
            }

            if (!result.contains(a.genes.first())) {
                result.add(0, a.genes.first())
            }

            if (!result.contains(a.genes.last())) {
                result.add(result.size, a.genes.last())
            }

            Genotype(result, 0.0)
        },
        {
            val result = arrayListOf<Int>()
            val genesPool = arrayListOf<Int>()

            for (i in it.genes.first() until it.genes.last()) {
                genesPool.add(i)
            }

            for (i in 0 until it.genes.size) {
                result.add(it.genes[i])
                genesPool.remove(it.genes[i])
            }

            val pAdd = 0.2
            val pRemove = 0.2
            val pSwap = 0.2

            var add = random.nextDouble() < pAdd
            var remove = random.nextDouble() < pRemove
            var swap = random.nextDouble() < pSwap

            while (add || remove || swap) {
                if (add) {
                    val rInd = random.nextInt(1, result.size - 1)
                    val gInd = random.nextInt(0, genesPool.size - 1)

                    result.add(rInd, genesPool[gInd])
                    genesPool.removeAt(gInd)
                }
                if (remove) {
                    val rInd = random.nextInt(1, result.size - 1)

                    genesPool.add(result[rInd])
                    result.remove(rInd)
                }
                if (swap) {
                    val lind = random.nextInt(1, result.size - 1)
                    val rind = random.nextInt(1, result.size - 1)

                    if (lind != rind) {
                        val lval = result[lind]
                        val rval = result[rind]

                        result[rind] = lval
                        result[lind] = rval
                    }
                }

                add = random.nextDouble() < pAdd
                remove = random.nextDouble() < pRemove
                swap = random.nextDouble() < pSwap
            }

            Genotype(result, 0.0)
        },
        {
            val result = arrayListOf<Pair<Int, Int>>()

            for (i in 0 until 32) {
                val a = generate(it)
                val b = generate(it)

                if (a != b) {
                    result.add(Pair(a, b))
                }
            }

            result
        },
        {
            val result = arrayListOf<Int>()

            for (i in 0 until result.size) {
                if (random.nextDouble() < 0.20) {
                    result.add(i)
                }
            }

            result
        },
        {
            val fitness = arrayListOf<Double>()
            val element = arrayListOf<Int>()
            val result = arrayListOf<Int>()

            for (i in 0 until it.size) {
                fitness.add(it[i])
                element.add(i)
            }

            while (element.isNotEmpty() && result.size < 256) {
                val selected = generate(fitness)

                result.add(element[selected])
                fitness.removeAt(selected)
                element.removeAt(selected)
            }

            result
        }
    )

    for (i in 0 until 512) {
        generation.update()
    }
}