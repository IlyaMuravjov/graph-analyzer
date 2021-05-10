package spbu_coding.graph_analyzer.controller

import javafx.beans.value.ObservableValue
import javafx.scene.layout.Pane
import spbu_coding.graph_analyzer.utils.onNull
import spbu_coding.graph_analyzer.utils.plus
import spbu_coding.graph_analyzer.utils.subPane
import spbu_coding.graph_analyzer.view.EdgePane
import spbu_coding.graph_analyzer.view.EdgeView
import tornadofx.*

private const val MAX_MILLIS_PER_EDGE_RENDER_CYCLE = 50L
const val EDGES_PER_PANE = 5000

class EdgeRenderingController(
    private val parentPane: Pane,
    edges: Collection<EdgeView>
) : Controller() {
    private val nonRenderedEdgeChunks = edges.shuffled().chunked(EDGES_PER_PANE).reversed().toMutableList()
    private val nonRenderedPanes = mutableListOf<EdgePane>()
    private val renderedPanes = mutableListOf<EdgePane>()

    val prefEdgesRenderedProperty = longProperty(0)
    var prefEdgesRendered by prefEdgesRenderedProperty

    private val prefPanesRenderedObservableValue = prefEdgesRenderedProperty.longBinding {
        prefEdgesRendered / EDGES_PER_PANE + if (prefEdgesRendered % EDGES_PER_PANE == 0L) 0 else 1
    }.onChange { renderEdges() }
    private val prefPanesRendered by prefPanesRenderedObservableValue

    private val edgesRenderedProperty = longProperty(0)
    val edgesRenderedObservableValue: ObservableValue<Number> = edgesRenderedProperty
    var edgesRendered by edgesRenderedProperty
        private set

    private var isRunning = false

    init {
        parentPane.sceneProperty().onNull { isRunning = false }
        FX.primaryStage.onCloseRequest += { isRunning = false }
    }

    private fun renderEdges() {
        if (!isRunning) {
            isRunning = true
            doRenderEdges()
        }
    }

    private fun doRenderEdges() {
        if (!isRunning) return
        val millisBefore = System.currentTimeMillis()

        fun runLaterIfTimeExceeded() = (System.currentTimeMillis() - millisBefore > MAX_MILLIS_PER_EDGE_RENDER_CYCLE)
            .also { if (it) runLater { doRenderEdges() } }

        while (renderedPanes.size < prefPanesRendered) {
            if (runLaterIfTimeExceeded()) return
            val pane = nonRenderedPanes.removeLastOrNull() ?: createNonRenderedEdgePane() ?: break
            pane.render()
            renderedPanes.add(pane)
            edgesRendered += pane.edgeCount
        }
        while (renderedPanes.size > prefPanesRendered) {
            if (runLaterIfTimeExceeded()) return
            val pane = renderedPanes.removeLast()
            pane.unrender()
            nonRenderedPanes.add(pane)
            edgesRendered -= pane.edgeCount
        }
        isRunning = false
    }

    private fun createNonRenderedEdgePane(): EdgePane? = nonRenderedEdgeChunks.removeLastOrNull()?.let { chunk ->
        EdgePane(chunk).apply { parentPane.subPane(this) }
    }
}
