package spbu_coding.graph_analyzer.model.impl

import spbu_coding.graph_analyzer.model.Graph
import spbu_coding.graph_analyzer.model.GraphAlgorithm
import spbu_coding.graph_analyzer.model.Vertex
import spbu_coding.graph_analyzer.utils.Props
import spbu_coding.graph_analyzer.utils.RefreshablePropsHolderImpl

abstract class AbstractGraphAlgorithm<V, P : Props<P>>(
    private val syncGraph: Graph<Vertex>,
    uiProps: P
) : GraphAlgorithm<V, P>, RefreshablePropsHolderImpl<P>(uiProps) {
    override var terminated = false
        protected set
    override var graph: Graph<V> = syncGraph.map { adaptVertex(it) }
        protected set

    protected abstract fun adaptVertex(vertex: Vertex): V

    override fun refreshGraph() {
        graph = syncGraph.map { adaptVertex(it) }
    }
}
