package spbu_coding.graph_analyzer.model.impl.algorithm.community

import spbu_coding.graph_analyzer.model.Graph
import spbu_coding.graph_analyzer.model.Vertex
import spbu_coding.graph_analyzer.model.VertexCommunity
import spbu_coding.graph_analyzer.model.impl.algorithm.AbstractGraphAlgorithm
import spbu_coding.graph_analyzer.model.impl.map
import spbu_coding.graph_analyzer.utils.CopyablePropsHolder

abstract class AbstractCommunityAlgorithm<out C : VertexCommunity, P : CopyablePropsHolder<P>>(
    displayName: String,
    protected val uiGraph: Graph<Vertex>,
    uiProps: P
) : AbstractGraphAlgorithm<C, P>(displayName, uiProps) {
    protected abstract fun getVertexCommunity(vertex: Vertex): C

    protected fun communityGraph(): Graph<C> = uiGraph.map { v -> getVertexCommunity(v).also { v.community = it } }
}
