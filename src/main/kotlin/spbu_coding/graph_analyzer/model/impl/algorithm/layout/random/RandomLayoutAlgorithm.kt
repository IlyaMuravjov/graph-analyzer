package spbu_coding.graph_analyzer.model.impl.algorithm.layout.random

import javafx.geometry.Point2D
import spbu_coding.graph_analyzer.model.Graph
import spbu_coding.graph_analyzer.model.Vertex
import spbu_coding.graph_analyzer.model.impl.VertexLayoutImpl
import spbu_coding.graph_analyzer.model.impl.algorithm.layout.AbstractLayoutAlgorithm
import kotlin.random.Random

class RandomLayoutAlgorithm(
    uiGraph: Graph<Vertex>,
) : AbstractLayoutAlgorithm<VertexLayoutImpl, RandomLayoutProps>("Random", uiGraph, RandomLayoutProps()) {
    private var graph = adaptedGraph()

    override fun getVertexLayout(vertex: Vertex) = VertexLayoutImpl(vertex.layout.pos, vertex.layout.radius)

    override fun refreshGraph() {
        graph = adaptedGraph()
    }

    override fun runIteration() {
        graph.vertices.forEach { it.pos = Point2D(nextCoordinate(), nextCoordinate()) }
        terminated = true
    }

    private fun nextCoordinate() = Random.nextDouble(-props.maxCoordinateDeviation, props.maxCoordinateDeviation)
}
