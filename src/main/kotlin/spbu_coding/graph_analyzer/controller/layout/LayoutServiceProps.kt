package spbu_coding.graph_analyzer.controller.layout

import org.controlsfx.control.PropertySheet
import spbu_coding.graph_analyzer.utils.Props
import spbu_coding.graph_analyzer.utils.beanProperty
import tornadofx.getValue
import tornadofx.intProperty
import tornadofx.setValue

private const val MAX_MILLIS_PER_PROPS_UPDATES = 1000.0

data class LayoutServiceProps(var maxUiUpdatesPerSecond: Double = 50.0) : Props<LayoutServiceProps> {
    val iterationsPerSecondProperty = intProperty(0)
    fun iterationsPerSecondProperty() = iterationsPerSecondProperty
    var iterationsPerSecond by iterationsPerSecondProperty

    val minMillisPerUiUpdate get() = 1000 / maxUiUpdatesPerSecond
    val minMillisPerPropsUpdate get() = minMillisPerUiUpdate.coerceAtMost(MAX_MILLIS_PER_PROPS_UPDATES)

    override val propertySheetItems: List<PropertySheet.Item>
        get() = listOf(
            beanProperty("maxUiUpdatesPerSecond", "Max UI updates per second"),
            beanProperty("iterationsPerSecond", "Iterations per second", readOnly = true)
        )

    override fun copyWritableProps() = copy()
}
