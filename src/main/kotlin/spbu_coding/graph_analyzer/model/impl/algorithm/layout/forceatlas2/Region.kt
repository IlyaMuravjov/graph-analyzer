package spbu_coding.graph_analyzer.model.impl.algorithm.layout.forceatlas2

import javafx.geometry.Point2D
import tornadofx.div
import tornadofx.plus
import tornadofx.times

class Region(private val vertices: Collection<ForceAtlas2Vertex>) {
    private val mass = vertices.sumByDouble { it.mass }
    private val massCenter = vertices.fold(Point2D.ZERO) { acc, v -> acc + v.mass * v.pos } / mass
    private val radius = vertices.map { massCenter.distance(it.pos) }.maxOrNull() ?: Double.MIN_VALUE
    private val subregions: Collection<Region> =
        if (vertices.size <= 1) emptyList()
        else vertices.partition { it.pos.x < massCenter.x }.toList()
            .flatMap { side -> side.partition { v -> v.pos.y < massCenter.y }.toList() }
            .map { Region(it) }

    fun repulse(vertex: ForceAtlas2Vertex, repulsion: RepulsionForce, theta: Double) {
        if (vertices.size <= 1) repulsion.apply(vertex, vertices.firstOrNull() ?: return)
        else {
            val distance = massCenter.distance(vertex.pos)
            if (distance * theta > radius) repulsion.apply(vertex, massCenter, mass)
            else subregions.forEach { it.repulse(vertex, repulsion, theta) }
        }
    }
}
