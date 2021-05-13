package spbu_coding.graph_analyzer.model.impl.algorithm

import spbu_coding.graph_analyzer.model.Graph
import spbu_coding.graph_analyzer.model.GraphAlgorithm
import spbu_coding.graph_analyzer.model.Vertex
import spbu_coding.graph_analyzer.model.impl.map
import spbu_coding.graph_analyzer.utils.CopyablePropertySheetItemsHolder
import spbu_coding.graph_analyzer.utils.PropertySheetItemsHolder

abstract class AbstractGraphAlgorithm<out V, P : CopyablePropertySheetItemsHolder<P>>(
    override val displayName: String,
    protected val uiGraph: Graph<Vertex>,
    protected val uiProps: P
) : GraphAlgorithm, PropertySheetItemsHolder by uiProps {
    protected var lastReset: Long = System.currentTimeMillis()
    override var terminated = false
        protected set
    protected var props: P = uiProps.copyWritableProps()

    protected abstract fun adaptVertex(vertex: Vertex): V

    override fun reset() {
        terminated = false
        lastReset = System.currentTimeMillis()
    }

    override fun refreshProps() {
        props = uiProps.copyWritableProps()
    }

    protected fun adaptedGraph() = uiGraph.map { adaptVertex(it) }

    override fun toString() = displayName
}
