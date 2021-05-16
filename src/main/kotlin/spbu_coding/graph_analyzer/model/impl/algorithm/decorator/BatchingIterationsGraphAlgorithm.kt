package spbu_coding.graph_analyzer.model.impl.algorithm.decorator

import javafx.concurrent.Task
import org.controlsfx.control.PropertySheet
import spbu_coding.graph_analyzer.model.GraphAlgorithm
import spbu_coding.graph_analyzer.model.impl.algorithm.decorator.BatchingIterationsGraphAlgorithm.Props
import spbu_coding.graph_analyzer.utils.CopyablePropsHolder

private const val MAX_MILLIS_PER_UI_UPDATES = 1000.0

fun GraphAlgorithm.batchIterations(): GraphAlgorithm = BatchingIterationsGraphAlgorithm(this)

class BatchingIterationsGraphAlgorithm(
    delegate: GraphAlgorithm
) : AbstractGraphAlgorithmDecorator<Props>(delegate, Props()) {
    override fun runIteration(task: Task<GraphAlgorithm.IterationResult>): GraphAlgorithm.IterationResult {
        val millisBefore = System.currentTimeMillis()
        var result: GraphAlgorithm.IterationResult
        do {
            if (task.isCancelled) return GraphAlgorithm.IterationResult.TERMINATED
            result = delegate.runIteration(task)
        } while (
            result == GraphAlgorithm.IterationResult.UNFINISHED &&
            System.currentTimeMillis() - millisBefore < props.minMillisPerUiUpdate
        )
        return result
    }

    data class Props(var maxUIUpdatesPerSecond: Double = 50.0) : CopyablePropsHolder<Props> {
        val minMillisPerUiUpdate get() = (1000.0 / maxUIUpdatesPerSecond).coerceAtMost(MAX_MILLIS_PER_UI_UPDATES)

        override val propertySheetItems: List<PropertySheet.Item>
            get() = listOf(beanProperty("maxUIUpdatesPerSecond", "Max UI updates per second"))

        override fun copyInputProps() = copy()
    }
}
