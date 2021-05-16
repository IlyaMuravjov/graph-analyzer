package spbu_coding.graph_analyzer.model.impl.algorithm.decorator

import org.controlsfx.control.PropertySheet
import spbu_coding.graph_analyzer.model.GraphAlgorithm
import spbu_coding.graph_analyzer.utils.CopyablePropsHolder

abstract class AbstractGraphAlgorithmDecorator<P : CopyablePropsHolder<P>>(
    protected val delegate: GraphAlgorithm,
    protected val uiProps: P,
) : GraphAlgorithm by delegate {
    protected var props: P = uiProps.copyInputProps()
        private set

    override val propertySheetItems: List<PropertySheet.Item>
        get() = uiProps.propertySheetItems + delegate.propertySheetItems

    override fun refreshInputProps() {
        props = uiProps.copyInputProps()
        delegate.refreshInputProps()
    }
}
