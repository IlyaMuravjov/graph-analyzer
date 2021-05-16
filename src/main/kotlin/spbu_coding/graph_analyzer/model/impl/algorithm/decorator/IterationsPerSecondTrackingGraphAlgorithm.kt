package spbu_coding.graph_analyzer.model.impl.algorithm.decorator

import javafx.concurrent.Task
import org.controlsfx.control.PropertySheet
import spbu_coding.graph_analyzer.model.GraphAlgorithm
import spbu_coding.graph_analyzer.model.impl.algorithm.decorator.IterationsPerSecondTrackingGraphAlgorithm.Props
import spbu_coding.graph_analyzer.utils.CopyablePropsHolder
import tornadofx.getValue
import tornadofx.intProperty
import tornadofx.setValue

fun GraphAlgorithm.trackIterationsPerSecond(): GraphAlgorithm = IterationsPerSecondTrackingGraphAlgorithm(this)

class IterationsPerSecondTrackingGraphAlgorithm(
    delegate: GraphAlgorithm
) : AbstractGraphAlgorithmDecorator<Props>(delegate, Props()) {
    private val lastSecondIterationMillis = ArrayDeque<Long>()

    override fun updateOutputProps() {
        uiProps.iterationsPerSecond = lastSecondIterationMillis.size
        delegate.updateOutputProps()
    }

    override fun fixAfterInterruption(): GraphAlgorithm.IterationResult {
        uiProps.iterationsPerSecond = 0
        lastSecondIterationMillis.clear()
        return delegate.fixAfterInterruption()
    }

    override fun runIteration(task: Task<GraphAlgorithm.IterationResult>): GraphAlgorithm.IterationResult {
        lastSecondIterationMillis.addFirst(System.currentTimeMillis())
        val result = delegate.runIteration(task)
        while (lastSecondIterationMillis.isNotEmpty() && System.currentTimeMillis() - lastSecondIterationMillis.last() > 1000L)
            lastSecondIterationMillis.removeLast()
        return result
    }

    class Props : CopyablePropsHolder<Props> {
        val iterationsPerSecondProperty = intProperty(0)
        fun iterationsPerSecondProperty() = iterationsPerSecondProperty
        var iterationsPerSecond by iterationsPerSecondProperty

        override val propertySheetItems: List<PropertySheet.Item>
            get() = listOf(beanProperty("iterationsPerSecond", "Iterations per second", readOnly = true))

        override fun copyInputProps() = Props() // no input props to copy
    }
}
