package spbu_coding.graph_analyzer.model.impl.algorithm.layout.random

import org.controlsfx.control.PropertySheet
import spbu_coding.graph_analyzer.utils.CopyablePropertySheetItemsHolder

data class RandomLayoutProps(
    var maxCoordinateDeviation: Double = 100.0
) : CopyablePropertySheetItemsHolder<RandomLayoutProps> {
    override val propertySheetItems: List<PropertySheet.Item>
        get() = listOf(beanProperty("maxCoordinateDeviation", "Max coordinate deviation"))

    override fun copyWritableProps() = copy()
}
