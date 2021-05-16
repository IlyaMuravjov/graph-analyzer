package spbu_coding.graph_analyzer.controller.algorithm

import javafx.beans.property.ReadOnlyProperty
import javafx.concurrent.Service
import javafx.concurrent.Task
import spbu_coding.graph_analyzer.model.GraphAlgorithm
import spbu_coding.graph_analyzer.utils.*
import spbu_coding.graph_analyzer.view.GraphView
import tornadofx.*
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class GraphAlgorithmCategoryService(
    private val controller: GraphAlgorithmCategoryController,
    graphView: GraphView
) : Service<GraphAlgorithm.IterationResult>() {
    val category get() = controller.category
    val algorithms get() = controller.algorithms

    val algorithmProperty = objectProperty(algorithms.first()).apply { onChange { state = State.READY } }
    var algorithm: GraphAlgorithm by algorithmProperty

    private val _stateProperty = objectProperty(State.READY)
    val stateProperty: ReadOnlyProperty<State> = _stateProperty
    var state: State by _stateProperty
        private set

    val observablePropertySheetItems = algorithmProperty.toObservableList { it.propertySheetItems }

    private val runsUninterrupted = AtomicBoolean(false)

    init {
        graphView.sceneProperty().onNull { cancel() }
        executor = Executors.newSingleThreadExecutor {
            Thread(it).apply {
                isDaemon = true
                name = "${this@GraphAlgorithmCategoryService}-$name"
            }
        }
    }

    fun toggle() {
        if (state == State.RUNNING) {
            cancel()
            if (!runsUninterrupted.compareAndSet(true, false)) toggleOff()
        } else {
            if (state != State.PAUSED) algorithm.reset()
            state = State.RUNNING
            reset()
            start()
        }
    }

    override fun scheduled() {
        controller.refreshGraph()
        algorithm.refreshGraph()
        algorithm.refreshInputProps()
        runsUninterrupted.set(true)
    }

    override fun createTask(): Task<GraphAlgorithm.IterationResult> = object : Task<GraphAlgorithm.IterationResult>() {
        override fun call() = try {
            algorithm.runIteration(this)
        } finally {
            if (!runsUninterrupted.compareAndSet(true, false)) runLater { toggleOff() }
        }
    }

    override fun succeeded() {
        algorithm.updateOutputProps()
        controller.refreshView()
        if (state != State.RUNNING) return
        state = value.toState()
        if (state == State.RUNNING) {
            reset()
            start()
        }
    }

    override fun failed() {
        state = State.TERMINATED
        throw RuntimeException("Exception in service [$this], algorithm: [$algorithm]", exception)
    }

    override fun toString(): String = "${controller.category}Service"

    private fun toggleOff() {
        state = algorithm.fixAfterInterruption().toState()
    }

    private fun GraphAlgorithm.IterationResult.toState() = when (this) {
        GraphAlgorithm.IterationResult.UNFINISHED -> State.RUNNING
        GraphAlgorithm.IterationResult.PAUSED -> State.PAUSED
        GraphAlgorithm.IterationResult.TERMINATED -> State.TERMINATED
    }

    enum class State(val actionName: String) {
        READY("Run"),
        RUNNING("Stop"),
        PAUSED("Continue"),
        TERMINATED("Rerun");
    }
}
