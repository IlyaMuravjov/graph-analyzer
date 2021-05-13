package spbu_coding.graph_analyzer.model.impl.algorithm.layout.forceatlas2

import spbu_coding.graph_analyzer.model.Graph
import spbu_coding.graph_analyzer.model.Vertex
import spbu_coding.graph_analyzer.model.impl.algorithm.layout.AbstractLayoutAlgorithm
import tornadofx.Vector2D
import tornadofx.plus
import tornadofx.times
import kotlin.math.sqrt

class ForceAtlas2(
    uiGraph: Graph<Vertex>
) : AbstractLayoutAlgorithm<ForceAtlas2Vertex, ForceAtlas2Props>("ForceAtlas2", uiGraph, ForceAtlas2Props(uiGraph)) {
    private var temperature = 1.0
    private var temperatureEfficiency = 1.0
    private var graph = adaptedGraph()

    override fun reset() {
        temperature = 1.0
        temperatureEfficiency = 1.0
        graph.vertices.forEach { it.velocity = Vector2D.ZERO }
        super.reset()
    }

    override fun getVertexLayout(vertex: Vertex): ForceAtlas2Vertex =
        (vertex.layout as? ForceAtlas2Vertex) ?: ForceAtlas2Vertex(vertex.layout)

    override fun refreshGraph() {
        graph = adaptedGraph()
        graph.vertices.forEach { it.mass = 1.0 }
        graph.edges.forEach {
            it.from.mass++
            it.to.mass++
        }
    }

    override fun runIteration() {
        ensureSafePositions(graph.vertices)
        graph.vertices.forEach {
            it.oldVelocity = it.velocity
            it.velocity = Vector2D.ZERO
        }
        val attraction = buildAttraction(
            type = props.attractionType,
            dissuadeHubs = props.dissuadeHubs,
            preventOverlap = props.preventOverlap,
            coefficient = if (props.dissuadeHubs) graph.edges.size / (2.0 * graph.vertices.size) else 1.0,
            edgeWeightExponent = props.edgeWeightExponent
        )
        val repulsion = buildRepulsion(
            preventOverlap = props.preventOverlap,
            coefficient = props.scaling
        )
        val gravity = buildGravity(
            isStrong = props.strongGravity,
            coefficient = props.gravityCoefficient / props.scaling
        )
        val rootRegion = if (props.barnesHutApproximation) Region(graph.vertices) else null
        val vertexStream = if (props.multithreaded) graph.vertices.parallelStream() else graph.vertices.stream()
        vertexStream.forEach { vertex ->
            if (props.barnesHutApproximation) rootRegion!!.repulse(vertex, repulsion, props.barnesHutTheta)
            else graph.vertices.forEach { otherVertex -> repulsion.apply(vertex, otherVertex) }
            gravity.apply(vertex)
        }
        graph.edges.forEach { attraction.apply(it) }
        val totalSwinging = graph.vertices.sumByDouble { it.swinging }
        val totalEffectiveTraction = graph.vertices.sumByDouble { it.effectiveTraction }
        val estimatedOptimalTolerance = 0.05 * sqrt(graph.vertices.size.toDouble())
        var tolerance = props.tolerance *
                (estimatedOptimalTolerance * totalEffectiveTraction / graph.vertices.size / graph.vertices.size)
                    .coerceAtMost(10.0).coerceAtLeast(sqrt(estimatedOptimalTolerance))
        val minTemperatureEfficiency = 0.05
        if (totalSwinging / totalEffectiveTraction > 2.0) {
            if (temperatureEfficiency > minTemperatureEfficiency) temperatureEfficiency *= 0.5
            tolerance = tolerance.coerceAtLeast(props.tolerance)
        }
        val targetTemperature = tolerance * temperatureEfficiency * totalEffectiveTraction / totalSwinging
        if (totalSwinging > tolerance * totalEffectiveTraction) {
            if (temperatureEfficiency > minTemperatureEfficiency) temperatureEfficiency *= 0.7
        } else if (temperature < 1000) temperatureEfficiency *= 1.3
        temperature = targetTemperature.coerceAtMost(1.5 * temperature)
        graph.vertices.forEach { vertex ->
            var factor = temperature / (1.0 + sqrt(temperature * vertex.swinging))
            if (props.preventOverlap) {
                factor *= 0.1
                factor.coerceAtMost(10.0 / vertex.velocity.magnitude())
            }
            vertex.pos += factor * vertex.velocity
        }
    }
}
