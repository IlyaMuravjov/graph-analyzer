package spbu_coding.graph_analyzer.model.impl.algorithm.community

import spbu_coding.graph_analyzer.model.Graph
import spbu_coding.graph_analyzer.model.Vertex
import spbu_coding.graph_analyzer.model.VertexCommunity
import spbu_coding.graph_analyzer.model.impl.algorithm.AbstractGraphAlgorithm
import spbu_coding.graph_analyzer.utils.CopyablePropertySheetItemsHolder

abstract class AbstractCommunityAlgorithm<out C : VertexCommunity, P : CopyablePropertySheetItemsHolder<P>>(
    displayName: String,
    uiGraph: Graph<Vertex>,
    uiProps: P
) : AbstractGraphAlgorithm<C, P>(displayName, uiGraph, uiProps) {
    protected abstract fun getVertexLayout(vertex: Vertex): C

    override fun adaptVertex(vertex: Vertex): C = getVertexLayout(vertex).also { vertex.community = it }
}
