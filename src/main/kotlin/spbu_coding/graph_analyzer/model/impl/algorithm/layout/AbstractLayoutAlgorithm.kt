package spbu_coding.graph_analyzer.model.impl.algorithm.layout

import spbu_coding.graph_analyzer.model.Graph
import spbu_coding.graph_analyzer.model.Vertex
import spbu_coding.graph_analyzer.model.VertexLayout
import spbu_coding.graph_analyzer.model.impl.algorithm.AbstractGraphAlgorithm
import spbu_coding.graph_analyzer.utils.CopyablePropertySheetItemsHolder

abstract class AbstractLayoutAlgorithm<L : VertexLayout, P : CopyablePropertySheetItemsHolder<P>>(
    displayName: String,
    uiGraph: Graph<Vertex>,
    uiProps: P
) : AbstractGraphAlgorithm<L, P>(displayName, uiGraph, uiProps) {
    protected abstract fun getVertexLayout(vertex: Vertex): L

    override fun adaptVertex(vertex: Vertex): L = getVertexLayout(vertex).also { vertex.layout = it }
}
