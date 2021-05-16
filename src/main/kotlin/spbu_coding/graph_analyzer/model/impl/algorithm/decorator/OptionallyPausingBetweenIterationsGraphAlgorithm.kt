package spbu_coding.graph_analyzer.model.impl.algorithm.decorator

import javafx.concurrent.Task
import org.controlsfx.control.PropertySheet
import spbu_coding.graph_analyzer.model.GraphAlgorithm
import spbu_coding.graph_analyzer.model.impl.algorithm.decorator.OptionallyPausingBetweenIterationsGraphAlgorithm.Props
import spbu_coding.graph_analyzer.utils.CopyablePropsHolder

fun GraphAlgorithm.optionallyPauseBetweenIterations(): GraphAlgorithm =
    OptionallyPausingBetweenIterationsGraphAlgorithm(this)

class OptionallyPausingBetweenIterationsGraphAlgorithm(
    delegate: GraphAlgorithm
) : AbstractGraphAlgorithmDecorator<Props>(delegate, Props()) {
    override fun runIteration(task: Task<GraphAlgorithm.IterationResult>): GraphAlgorithm.IterationResult {
        val result = delegate.runIteration(task)
        return if (props.pauseBetweenIterations && result == GraphAlgorithm.IterationResult.UNFINISHED)
            GraphAlgorithm.IterationResult.PAUSED
        else result
    }

    data class Props(var pauseBetweenIterations: Boolean = false) : CopyablePropsHolder<Props> {
        override val propertySheetItems: List<PropertySheet.Item>
            get() = listOf(beanProperty("pauseBetweenIterations", "Pause between iterations"))

        override fun copyInputProps() = copy()
    }
}
