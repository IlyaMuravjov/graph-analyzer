package spbu_coding.graph_analyzer.controller

import spbu_coding.graph_analyzer.view.VertexView
import tornadofx.Controller

class VertexMouseDragController : Controller() {
    fun registerVertex(vertexView: VertexView) {
        vertexView.setOnMousePressed {
            it.consume()
            vertexView.lastMouseReleasedMillis = Long.MAX_VALUE
        }
        vertexView.setOnMouseDragged {
            it.consume()
            vertexView.circle.centerX = it.x
            vertexView.circle.centerY = it.y
        }
        vertexView.setOnMouseReleased {
            it.consume()
            vertexView.lastMouseReleasedMillis = System.currentTimeMillis()
        }
    }
}
