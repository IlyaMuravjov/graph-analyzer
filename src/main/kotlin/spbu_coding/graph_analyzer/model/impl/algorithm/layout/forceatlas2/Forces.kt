package spbu_coding.graph_analyzer.model.impl.algorithm.layout.forceatlas2

import javafx.geometry.Point2D
import tornadofx.minus
import tornadofx.plus
import tornadofx.times
import kotlin.math.ln1p
import kotlin.math.pow

fun interface AttractionForce {
    fun apply(edge: ForceAtlas2Edge)
}

enum class AttractionType(val displayName: String) {
    LINEAR("linear"),
    LOGARITHMIC("logarithmic");

    override fun toString() = displayName
}

fun buildAttraction(
    type: AttractionType,
    dissuadeHubs: Boolean,
    preventOverlap: Boolean,
    coefficient: Double,
    edgeWeightExponent: Double
) = AttractionForce { edge ->
    with(edge) {
        val vector = to.pos - from.pos
        var distance = vector.magnitude()
        if (preventOverlap) distance -= from.radius + to.radius
        if (distance > 0) {
            var factor = coefficient
            factor *= when (edgeWeightExponent) {
                0.0 -> 1.0
                1.0 -> weight
                else -> weight.pow(edgeWeightExponent)
            }
            factor *= when (type) {
                AttractionType.LINEAR -> 1.0
                AttractionType.LOGARITHMIC -> ln1p(distance) / distance
            }
            if (dissuadeHubs) factor /= from.mass
            val force = factor * vector
            from.velocity += force
            to.velocity -= force
        }
    }
}


fun interface RepulsionForce {
    fun apply(vertex: ForceAtlas2Vertex, pos: Point2D, mass: Double, radius: Double)

    fun apply(vertex: ForceAtlas2Vertex, otherVertex: ForceAtlas2Vertex) =
        apply(vertex, otherVertex.pos, otherVertex.mass, otherVertex.radius)

    fun apply(vertex: ForceAtlas2Vertex, pos: Point2D, mass: Double) =
        apply(vertex, pos, mass, -vertex.radius)
}

private const val ON_OVERLAP_REPULSION_MULTIPLIER = 4

fun buildRepulsion(preventOverlap: Boolean, coefficient: Double) = RepulsionForce { vertex, pos, mass, radius ->
    val vector = pos - vertex.pos
    var distance = vector.magnitude()
    if (preventOverlap) distance -= vertex.radius + radius
    var factor = coefficient * vertex.mass * mass
    if (distance > 0.5) factor /= distance * distance
    else factor *= ON_OVERLAP_REPULSION_MULTIPLIER
    vertex.velocity -= factor * vector
}


fun interface Gravity {
    fun apply(vertex: ForceAtlas2Vertex)
}

fun buildGravity(isStrong: Boolean, coefficient: Double) = Gravity { vertex ->
    var factor = coefficient
    if (!isStrong) factor /= vertex.pos.magnitude()
    vertex.velocity -= factor * vertex.pos
}
