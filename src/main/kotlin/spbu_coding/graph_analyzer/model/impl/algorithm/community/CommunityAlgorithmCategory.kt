package spbu_coding.graph_analyzer.model.impl.algorithm.community

import spbu_coding.graph_analyzer.model.Graph
import spbu_coding.graph_analyzer.model.GraphAlgorithm
import spbu_coding.graph_analyzer.model.GraphAlgorithmCategory
import spbu_coding.graph_analyzer.model.Vertex
import spbu_coding.graph_analyzer.model.impl.algorithm.community.louvain.LouvainAlgorithm

object CommunityAlgorithmCategory : GraphAlgorithmCategory {
    override val displayName get() = "Community"

    override fun createAlgorithms(graph: Graph<Vertex>): List<GraphAlgorithm> = listOf(LouvainAlgorithm(graph))
}
