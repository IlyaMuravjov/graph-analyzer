package spbu_coding.graph_analyzer.view

import javafx.scene.layout.Pane

class EdgePane(edges: List<EdgeView>) : Pane() {
    private val lines = edges.map { it.line() }.also { children.addAll(it) }
    val edgeCount get() = lines.size

    fun render() {
        isVisible = true
        lines.forEach { it.bindPosition() }
    }

    fun unrender() {
        isVisible = false
        lines.forEach { it.unbindPosition() }
    }
}
