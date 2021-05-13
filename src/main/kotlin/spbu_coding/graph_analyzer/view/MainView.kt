package spbu_coding.graph_analyzer.view

import javafx.scene.control.TabPane
import spbu_coding.graph_analyzer.controller.GraphSerializationController
import spbu_coding.graph_analyzer.controller.algorithm.CommunityService
import spbu_coding.graph_analyzer.controller.algorithm.LayoutService
import spbu_coding.graph_analyzer.model.GraphAnalysisType
import spbu_coding.graph_analyzer.utils.propertySheet
import spbu_coding.graph_analyzer.utils.zoomScrollPane
import tornadofx.*

class MainView : View() {
    private val serializationController = GraphSerializationController(this)
    private val graphViewProperty = serializationController.openedGraphProperty.objectBinding { GraphView(it!!) }

    override val root = borderpane {
        centerProperty().bind(graphViewProperty.objectBinding { graphView ->
            zoomScrollPane(graphView!!, scaleValue = 0.3) {
                style += "-fx-focus-color: transparent;"
                hvalue = (hmin + hmax) / 2
                vvalue = (vmin + vmax) / 2
            }
        })
        leftProperty().bind(graphViewProperty.objectBinding { graphView ->
            tabpane {
                tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
                tab("General") { propertySheet(graphView!!.props.propertySheetItems.toObservable()) }
                tabs.add(GraphAlgorithmCategoryTab(LayoutService(graphView!!)))
                tabs.add(GraphAlgorithmCategoryTab(CommunityService(graphView)))
            }
        })
        top = menubar {
            menu("File") {
                GraphAnalysisType.values().forEach { analysisType ->
                    item("Open $analysisType") {
                        action { serializationController.openFile(analysisType) }
                    }
                }
                GraphAnalysisType.values().forEach { analysisType ->
                    item("Save $analysisType as") {
                        action { serializationController.saveAsFile(analysisType) }
                    }
                }
            }
        }
    }

    init {
        serializationController.openedGraphViewProperty.bind(graphViewProperty)
        titleProperty.bind(serializationController.openedGraphTitleObservableValue
            .stringBinding { listOfNotNull("Graph analyzer", it).joinToString(" - ") })
    }
}
