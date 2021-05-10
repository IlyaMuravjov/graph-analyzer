package spbu_coding.graph_analyzer.model

import spbu_coding.graph_analyzer.utils.Props
import spbu_coding.graph_analyzer.utils.RefreshablePropsHolder
import spbu_coding.graph_analyzer.utils.SharingPropsFactory

typealias LayoutAlgorithmFactory = SharingPropsFactory<LayoutAlgorithm<*, *>, *>

interface LayoutAlgorithm<out L : VertexLayout, P : Props<P>> : GraphAlgorithm<L, P>

interface GraphAlgorithm<out V, P : Props<P>> : RefreshablePropsHolder<P> {
    val terminated: Boolean
    val graph: Graph<V>
    fun refreshGraph()
    fun runIteration()
}
