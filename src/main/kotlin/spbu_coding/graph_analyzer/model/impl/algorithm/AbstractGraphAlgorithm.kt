package spbu_coding.graph_analyzer.model.impl.algorithm

import spbu_coding.graph_analyzer.model.GraphAlgorithm
import spbu_coding.graph_analyzer.utils.CopyablePropsHolder
import spbu_coding.graph_analyzer.utils.PropsHolder

abstract class AbstractGraphAlgorithm<out V, P : CopyablePropsHolder<P>>(
    override val displayName: String,
    protected val uiProps: P
) : GraphAlgorithm, PropsHolder by uiProps {
    protected var lastResetMillis: Long = System.currentTimeMillis()
    protected var props: P = uiProps.copyInputProps()

    override fun reset() {
        lastResetMillis = System.currentTimeMillis()
    }

    override fun fixAfterInterruption(): GraphAlgorithm.IterationResult {
        reset()
        return GraphAlgorithm.IterationResult.TERMINATED
    }

    override fun refreshInputProps() {
        props = uiProps.copyInputProps()
    }

    override fun updateOutputProps() = Unit

    override fun toString() = displayName
}
