package spbu_coding.graph_analyzer.model.impl.algorithm.community.louvain

import javafx.concurrent.Task
import spbu_coding.graph_analyzer.model.Graph
import spbu_coding.graph_analyzer.model.GraphAlgorithm
import spbu_coding.graph_analyzer.model.Vertex
import spbu_coding.graph_analyzer.model.impl.algorithm.community.AbstractCommunityAlgorithm
import spbu_coding.graph_analyzer.utils.EmptyProps

class LouvainAlgorithm(
    uiGraph: Graph<Vertex>
) : AbstractCommunityAlgorithm<LouvainCommunity, EmptyProps>(
    "Louvain method",
    uiGraph,
    EmptyProps
) {
    private var nextId = 0
    private var totalEdgeWeight = 0.0
    private lateinit var vertices: Collection<LouvainCommunity>

    override fun getVertexCommunity(vertex: Vertex) = LouvainCommunity(nextId++)

    override fun reset() {
        nextId = 0
        totalEdgeWeight = 0.0
        val graph = communityGraph()
        vertices = graph.vertices
        graph.edges.forEach { edge ->
            edge.from.connectTo(edge.to, edge.weight)
            if (edge.from != edge.to) edge.to.connectTo(edge.from, edge.weight)
            totalEdgeWeight += edge.weight
        }
        super.reset()
    }

    override fun refreshGraph() {
        if (uiGraph.lastModifiedMillis > lastResetMillis) reset()
    }

    override fun runIteration(task: Task<GraphAlgorithm.IterationResult>): GraphAlgorithm.IterationResult {
        vertices.forEach { it.wrapInSuperCommunity(nextId++) }
        var isModifiedAtAll = false
        var iterations = 0
        do {
            var isModified = false
            for (vertex in vertices.shuffled()) {
                val bestSuperCommunity = vertex.edges
                    .map { (to, weight) -> to.superCommunity!! to weight }
                    .filter { (to, _) -> to != vertex.superCommunity }
                    .maxByOrNull { (to, _) ->
                        to.computeModularityGainOnInsertion(vertex, totalEdgeWeight) -
                                vertex.computeModularityGainOnRemoval(totalEdgeWeight)
                    }?.first ?: continue
                val bestCommunityModularity =
                    bestSuperCommunity.computeModularityGainOnInsertion(vertex, totalEdgeWeight) -
                            vertex.computeModularityGainOnRemoval(totalEdgeWeight)
                if (bestCommunityModularity > 0) {
                    vertex.superCommunity!!.removeVertex(vertex)
                    bestSuperCommunity.insertSubCommunity(vertex)
                    isModified = true
                    isModifiedAtAll = true
                }
            }
        } while (isModified && iterations++ < 100)
        vertices = vertices.mapTo(mutableSetOf()) { it.superCommunity!! }.onEach { it.condensateEdges() }
        return if (isModifiedAtAll) GraphAlgorithm.IterationResult.UNFINISHED else GraphAlgorithm.IterationResult.TERMINATED
    }
}
