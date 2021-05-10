package spbu_coding.graph_analyzer.controller.layout

import javafx.beans.value.ObservableValue
import spbu_coding.graph_analyzer.model.impl.layout.LayoutAlgorithmType
import spbu_coding.graph_analyzer.utils.listBinding
import spbu_coding.graph_analyzer.utils.onNull
import spbu_coding.graph_analyzer.view.GraphView
import tornadofx.Controller
import tornadofx.getValue
import tornadofx.objectProperty
import tornadofx.setValue
import java.util.concurrent.locks.ReentrantLock

class LayoutController(private val graph: GraphView) : Controller() {
    private val layoutLock = ReentrantLock()
    private val algorithmFactories = LayoutAlgorithmType.values().associateWith { it.createFactory(graph.graph) }
    private val serviceProps = LayoutServiceProps()
    private val serviceProperty = objectProperty<LayoutService?>(null)
    private var service: LayoutService? by serviceProperty
    private val algorithmFactory get() = algorithmFactories.getValue(algorithmType)

    val algorithmTypeProperty = objectProperty(LayoutAlgorithmType.RANDOM)
    var algorithmType: LayoutAlgorithmType by algorithmTypeProperty

    val propertySheetItems = algorithmTypeProperty.listBinding {
        serviceProps.propertySheetItems + algorithmFactory.propertySheetItems
    }

    val toggledObservableValue: ObservableValue<Boolean> = serviceProperty.isNotNull
    val toggled: Boolean by toggledObservableValue

    init {
        graph.sceneProperty().onNull { service?.cancel() }
        toggle()
        algorithmType = LayoutAlgorithmType.FORCE_ATLAS_2
    }

    fun toggle() {
        service?.cancel() ?: run {
            service = LayoutService(graph, algorithmFactory.create(), layoutLock, serviceProps).apply {
                start()
                setOnCancelled { if (service === this) service = null }
            }
        }
    }
}
