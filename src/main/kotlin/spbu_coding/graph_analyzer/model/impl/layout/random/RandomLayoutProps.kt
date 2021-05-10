package spbu_coding.graph_analyzer.model.impl.layout.random

import org.controlsfx.control.PropertySheet
import spbu_coding.graph_analyzer.utils.Props
import spbu_coding.graph_analyzer.utils.beanProperty

data class RandomLayoutProps(var maxCoordinateDeviation: Double = 100.0) : Props<RandomLayoutProps> {
    override fun copyWritableProps() = copy()

    override val propertySheetItems: List<PropertySheet.Item>
        get() = listOf(beanProperty("maxCoordinateDeviation", "Max coordinate deviation"))
}
