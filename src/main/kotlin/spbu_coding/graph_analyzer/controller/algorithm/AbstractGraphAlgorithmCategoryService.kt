package spbu_coding.graph_analyzer.controller.algorithm

import javafx.beans.value.ObservableBooleanValue
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
import java.util.concurrent.atomic.AtomicBoolean

abstract class AbstractGraphAlgorithmCategoryService<P : CopyablePropertySheetItemsHolder<P>>(
    protected val graphView: GraphView,
    final override val category: GraphAlgorithmCategory,
    protected val uiProps: P
) : Service<Unit>(), GraphAlgorithmCategoryService {
    final override val algorithms = category.createAlgorithms(graphView.graph)

    final override val algorithmProperty = objectProperty(algorithms.first()).apply { onChange { it!!.reset() } }
    final override val algorithm: GraphAlgorithm by algorithmProperty

    private var runsIterationsUninterrupted = AtomicBoolean(false)

    private val toggledProperty = booleanProperty(false)
    override val toggledObservableValue: ObservableBooleanValue = toggledProperty
    final override var toggled by toggledProperty
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
        if (toggled) cancel()
        else {
            toggled = true
            reset()
            start()
        }
    }

    protected open fun pausesAfterEachIteration() = false
    protected open fun refreshGraph() = algorithm.refreshGraph()
    protected open fun Task<Unit>.runIterations(): Unit = algorithm.runIteration()
    protected abstract fun refreshView()

    override fun scheduled() {
        refreshGraph()
        props = uiProps.copyWritableProps()
        algorithm.refreshProps()
        runsIterationsUninterrupted.set(true)
    }

    override fun createTask(): Task<Unit> = object : Task<Unit>() {
        override fun call() = try {
            runIterations()
        } finally {
            if (!runsIterationsUninterrupted.compareAndSet(true, false)) {
                algorithm.onInterruption()
                runLater { toggled = false }
            }
        }
    }

    override fun succeeded() {
        refreshView()
        if (algorithm.terminated || pausesAfterEachIteration()) toggled = false
        else {
            reset()
            start()
        }
    }

    override fun failed() {
        toggled = false
        throw RuntimeException("Exception in service [$this]", exception)
    }

    override fun cancelled() {
        if (!runsIterationsUninterrupted.compareAndSet(true, false)) {
            algorithm.onInterruption()
            toggled = false
        }
    }
}
