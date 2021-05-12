package spbu_coding.graph_analyzer.model.impl.algorithm

import spbu_coding.graph_analyzer.model.Graph
import spbu_coding.graph_analyzer.model.GraphAlgorithm
import spbu_coding.graph_analyzer.model.Vertex
import spbu_coding.graph_analyzer.model.impl.map
import spbu_coding.graph_analyzer.utils.CopyablePropertySheetItemsHolder
import spbu_coding.graph_analyzer.utils.PropertySheetItemsHolder

abstract class AbstractGraphAlgorithm<V, P : CopyablePropertySheetItemsHolder<P>>(
    override val displayName: String,
    protected val uiGraph: Graph<Vertex>,
    protected val uiProps: P
) : GraphAlgorithm, PropertySheetItemsHolder by uiProps {
    override var terminated = false
        protected set
    protected var graph: Graph<V> = uiGraph.map { adaptVertex(it) }
    protected var props: P = uiProps.copyWritableProps()

    protected abstract fun adaptVertex(vertex: Vertex): V

    override fun onInterruption() = reset()

    override fun refreshProps() {
        props = uiProps.copyWritableProps()
    }

    override fun refreshGraph() {
        graph = uiGraph.map { adaptVertex(it) }
    }

    override fun toString() = displayName
}
