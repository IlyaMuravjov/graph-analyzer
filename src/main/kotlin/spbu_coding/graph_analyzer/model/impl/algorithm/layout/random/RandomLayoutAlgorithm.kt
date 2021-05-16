package spbu_coding.graph_analyzer.model.impl.algorithm.layout.random

import javafx.concurrent.Task
import spbu_coding.graph_analyzer.model.Graph
import spbu_coding.graph_analyzer.model.GraphAlgorithm
import spbu_coding.graph_analyzer.model.Vertex
import spbu_coding.graph_analyzer.model.impl.VertexLayoutImpl
import spbu_coding.graph_analyzer.model.impl.algorithm.layout.AbstractLayoutAlgorithm

class RandomLayoutAlgorithm(
    uiGraph: Graph<Vertex>,
) : AbstractLayoutAlgorithm<VertexLayoutImpl, RandomLayoutProps>("Random", uiGraph, RandomLayoutProps()) {
    private var graph = layoutGraph()

    override fun getVertexLayout(vertex: Vertex) = VertexLayoutImpl(vertex.layout.pos, vertex.layout.radius)

    override fun refreshGraph() {
        graph = layoutGraph()
    }

    override fun runIteration(task: Task<GraphAlgorithm.IterationResult>): GraphAlgorithm.IterationResult {
        setRandomPositions(graph.vertices, props.maxCoordinateDeviation)
        return GraphAlgorithm.IterationResult.TERMINATED
    }
}
