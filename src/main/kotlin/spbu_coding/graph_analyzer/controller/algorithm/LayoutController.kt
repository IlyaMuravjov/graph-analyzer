package spbu_coding.graph_analyzer.controller.algorithm

import spbu_coding.graph_analyzer.model.impl.algorithm.layout.LayoutAlgorithmCategory
import spbu_coding.graph_analyzer.view.GraphView

class LayoutController(private val graphView: GraphView) : GraphAlgorithmCategoryController {
    override val category get() = LayoutAlgorithmCategory
    override val algorithms = category.createAlgorithms(graphView.graph)
    private var lastGraphRefreshMillis = 0L

    override fun refreshGraph() {
        lastGraphRefreshMillis = System.currentTimeMillis()
        graphView.vertexViews.forEach {
            it.vertex.layout.pos = it.pos
            it.vertex.layout.radius = it.radius
        }
    }

    override fun refreshView() = graphView.vertexViews
        .filter { it.lastMouseReleasedMillis < lastGraphRefreshMillis }
        .forEach { it.pos = it.vertex.layout.pos }
}
