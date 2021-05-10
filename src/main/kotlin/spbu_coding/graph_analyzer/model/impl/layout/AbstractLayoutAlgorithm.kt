package spbu_coding.graph_analyzer.model.impl.layout

import spbu_coding.graph_analyzer.model.Graph
import spbu_coding.graph_analyzer.model.LayoutAlgorithm
import spbu_coding.graph_analyzer.model.Vertex
import spbu_coding.graph_analyzer.model.VertexLayout
import spbu_coding.graph_analyzer.model.impl.AbstractGraphAlgorithm
import spbu_coding.graph_analyzer.utils.Props

abstract class AbstractLayoutAlgorithm<L : VertexLayout, P : Props<P>>(
    uiGraph: Graph<Vertex>,
    uiProps: P
) : LayoutAlgorithm<L, P>, AbstractGraphAlgorithm<L, P>(uiGraph, uiProps) {
    protected abstract fun getVertexLayout(vertex: Vertex): L

    override fun adaptVertex(vertex: Vertex): L = getVertexLayout(vertex).also { vertex.layout = it }
}
