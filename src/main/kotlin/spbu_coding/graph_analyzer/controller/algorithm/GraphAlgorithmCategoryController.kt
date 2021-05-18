package spbu_coding.graph_analyzer.controller.algorithm

import spbu_coding.graph_analyzer.model.GraphAlgorithm
import spbu_coding.graph_analyzer.model.GraphAlgorithmCategory

interface GraphAlgorithmCategoryController {
    val category: GraphAlgorithmCategory
    val algorithms: List<GraphAlgorithm>
    fun refreshGraph()
    fun refreshView()
}
