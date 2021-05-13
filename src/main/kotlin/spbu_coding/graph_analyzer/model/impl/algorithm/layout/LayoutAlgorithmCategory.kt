package spbu_coding.graph_analyzer.model.impl.algorithm.layout

import spbu_coding.graph_analyzer.model.Graph
import spbu_coding.graph_analyzer.model.GraphAlgorithmCategory
import spbu_coding.graph_analyzer.model.Vertex
import spbu_coding.graph_analyzer.model.impl.algorithm.layout.forceatlas2.ForceAtlas2
import spbu_coding.graph_analyzer.model.impl.algorithm.layout.random.RandomLayoutAlgorithm

object LayoutAlgorithmCategory : GraphAlgorithmCategory {
    override val displayName get() = "Layout"

    override fun createAlgorithms(graph: Graph<Vertex>) = listOf(ForceAtlas2(graph), RandomLayoutAlgorithm(graph))
}
