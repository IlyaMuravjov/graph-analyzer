package spbu_coding.graph_analyzer.model.impl.algorithm.layout.random

import spbu_coding.graph_analyzer.model.Graph
import spbu_coding.graph_analyzer.model.Vertex
import spbu_coding.graph_analyzer.model.impl.VertexLayoutImpl
import spbu_coding.graph_analyzer.model.impl.algorithm.layout.AbstractLayoutAlgorithm

class RandomLayoutAlgorithm(
    uiGraph: Graph<Vertex>,
) : AbstractLayoutAlgorithm<VertexLayoutImpl, RandomLayoutProps>("Random", uiGraph, RandomLayoutProps()) {
    private var graph = adaptedGraph()

    override fun getVertexLayout(vertex: Vertex) = VertexLayoutImpl(vertex.layout.pos, vertex.layout.radius)

    override fun refreshGraph() {
        graph = adaptedGraph()
    }

    override fun runIteration() {
        setRandomPositions(graph.vertices, props.maxCoordinateDeviation)
        terminated = true
    }
}
