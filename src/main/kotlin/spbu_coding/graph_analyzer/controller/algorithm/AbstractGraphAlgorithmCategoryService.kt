package spbu_coding.graph_analyzer.controller.algorithm

import javafx.beans.value.ObservableBooleanValue
import javafx.beans.value.ObservableValue
import javafx.concurrent.Service
import javafx.concurrent.Task
import spbu_coding.graph_analyzer.model.GraphAlgorithm
import spbu_coding.graph_analyzer.model.GraphAlgorithmCategory
import spbu_coding.graph_analyzer.utils.CopyablePropertySheetItemsHolder
import spbu_coding.graph_analyzer.utils.onNull
import spbu_coding.graph_analyzer.utils.toObservableList
import spbu_coding.graph_analyzer.view.GraphView
import tornadofx.*
import java.util.concurrent.Executors

abstract class AbstractGraphAlgorithmCategoryService<P : CopyablePropertySheetItemsHolder<P>>(
    protected val graphView: GraphView,
    final override val category: GraphAlgorithmCategory,
    protected val uiProps: P
) : Service<Unit>(), GraphAlgorithmCategoryService {
    final override val algorithms = category.createAlgorithms(graphView.graph)

    final override val algorithmProperty = objectProperty(algorithms.first().apply { reset() }).apply {
        onChange { resetAlgorithm() }
    }
    final override val algorithm: GraphAlgorithm by algorithmProperty

    private var runsIterations = false
    protected var pendingStopRequest = false
        private set

    private val toggledProperty = booleanProperty(false)
    override val toggledObservableValue: ObservableBooleanValue = toggledProperty
    final override var toggled by toggledProperty
        private set

    private var terminatedProperty = booleanProperty(algorithm.terminated)
    override val terminatedObservableValue: ObservableValue<Boolean> = terminatedProperty
    final override var terminated by terminatedProperty
        private set

    protected var props: P = uiProps.copyWritableProps()

    override val observablePropertySheetItems = algorithmProperty.toObservableList {
        uiProps.propertySheetItems + it.propertySheetItems
    }

    init {
        graphView.sceneProperty().onNull { cancel() }
        executor = Executors.newSingleThreadExecutor {
            Thread(it).apply {
                isDaemon = true
                name = "${this@AbstractGraphAlgorithmCategoryService.javaClass.name}-$name"
            }
        }
    }

    override fun toggle() {
        if (toggled) {
            if (runsIterations) pendingStopRequest = true
            else {
                cancel()
                toggled = false
            }
        } else {
            if (terminated) resetAlgorithm()
            if (terminated) return
            toggled = true
            reset()
            start()
        }
    }

    protected open fun pausesBetweenIterations() = false
    protected open fun refreshGraph() = algorithm.refreshGraph()
    protected open fun runIterations(): Unit = algorithm.runIteration()
    protected abstract fun refreshView()

    override fun scheduled() {
        refreshGraph()
        props = uiProps.copyWritableProps()
        algorithm.refreshProps()
        runsIterations = true
    }

    override fun createTask(): Task<Unit> = object : Task<Unit>() {
        override fun call() = runIterations()
    }

    override fun succeeded() {
        runsIterations = false
        terminated = algorithm.terminated
        refreshView()
        if (pendingStopRequest || terminated || pausesBetweenIterations()) {
            pendingStopRequest = false
            toggled = false
        } else {
            reset()
            start()
        }
    }

    override fun failed() {
        pendingStopRequest = false
        toggled = false
        throw RuntimeException("Exception in service [$this]", exception)
    }

    private fun resetAlgorithm() {
        algorithm.reset()
        terminated = algorithm.terminated
    }
}
