package spbu_coding.graph_analyzer.controller.algorithm

import javafx.concurrent.Task
import spbu_coding.graph_analyzer.model.impl.algorithm.layout.LayoutAlgorithmCategory
import spbu_coding.graph_analyzer.utils.CopyablePropertySheetItemsHolder
import spbu_coding.graph_analyzer.utils.millisSince
import spbu_coding.graph_analyzer.view.GraphView
import tornadofx.getValue
import tornadofx.intProperty
import tornadofx.onChange
import tornadofx.setValue

class LayoutService(
    graphView: GraphView
) : AbstractGraphAlgorithmCategoryService<LayoutServiceProps>(
    graphView,
    LayoutAlgorithmCategory,
    LayoutServiceProps()
) {
    private var lastSecondIterationsMillis = mutableListOf<Long>()

    private var lastFrameMillis = 0L

    // false if only props should be refreshed
    private var uiUpdateRequested = true

    init {
        toggle()
        toggledObservableValue.onChange {
            if (!it) {
                lastSecondIterationsMillis.clear()
                uiProps.iterationsPerSecond = 0
            }
        }
    }

    override fun refreshGraph() {
        if (uiUpdateRequested) {
            if (lastFrameMillis == 0L) lastFrameMillis = System.currentTimeMillis()
            graphView.vertices.forEach {
                it.vertex.layout.pos = it.pos
                it.vertex.layout.radius = it.radius
            }
            super.refreshGraph()
        }
    }

    override fun Task<Unit>.runIterations() {
        do {
            if (isCancelled) return
            lastSecondIterationsMillis.add(System.currentTimeMillis())
            algorithm.runIteration()
        } while (millisSince(lastFrameMillis) < props.minMillisPerPropsUpdate)
        uiUpdateRequested = millisSince(lastFrameMillis) >= props.minMillisPerUiUpdate
    }

    override fun refreshView() {
        lastSecondIterationsMillis = lastSecondIterationsMillis.dropWhile { millisSince(it) > 1000L }.toMutableList()
        uiProps.iterationsPerSecond = lastSecondIterationsMillis.size
        if (uiUpdateRequested) {
            val previousFrameMillis = lastFrameMillis
            lastFrameMillis = System.currentTimeMillis()
            graphView.vertices
                .filter { it.lastDraggedMillis < previousFrameMillis }
                .forEach { it.pos = it.vertex.layout.pos }
        }
    }
}

private const val MAX_MILLIS_PER_PROPS_UPDATES = 1000.0

data class LayoutServiceProps(
    var maxUiUpdatesPerSecond: Double = 50.0
) : CopyablePropertySheetItemsHolder<LayoutServiceProps> {
    val iterationsPerSecondProperty = intProperty(0)
    fun iterationsPerSecondProperty() = iterationsPerSecondProperty
    var iterationsPerSecond by iterationsPerSecondProperty

    val minMillisPerUiUpdate get() = 1000 / maxUiUpdatesPerSecond
    val minMillisPerPropsUpdate get() = minMillisPerUiUpdate.coerceAtMost(MAX_MILLIS_PER_PROPS_UPDATES)

    override val propertySheetItems = listOf(
        beanProperty("maxUiUpdatesPerSecond", "Max UI updates per second"),
        beanProperty("iterationsPerSecond", "Iterations per second", readOnly = true)
    )

    override fun copyWritableProps() = copy()
}
