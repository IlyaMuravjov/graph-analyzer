package spbu_coding.graph_analyzer.model

import spbu_coding.graph_analyzer.utils.PropertySheetItemsHolder

interface GraphAlgorithm : PropertySheetItemsHolder {
    val displayName: String
    val terminated: Boolean
    fun reset()
    fun refreshProps()
    fun refreshGraph()
    fun runIteration()
}

interface GraphAlgorithmCategory {
    val displayName: String
    fun createAlgorithms(graph: Graph<Vertex>): List<GraphAlgorithm>
}
