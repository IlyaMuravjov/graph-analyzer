package spbu_coding.graph_analyzer.view

import javafx.scene.control.TabPane
import spbu_coding.graph_analyzer.controller.GraphSerializationController
import spbu_coding.graph_analyzer.model.GraphAnalysisType
import spbu_coding.graph_analyzer.utils.propertySheet
import spbu_coding.graph_analyzer.utils.zoomScrollPane
import spbu_coding.graph_analyzer.view.tab.LayoutTabView
import tornadofx.*

class MainView : View() {
    private val serializationController = GraphSerializationController(this)
    private val graphProperty = serializationController.openedGraphProperty.objectBinding { GraphView(it!!) }

    override val root = borderpane {
        centerProperty().bind(graphProperty.objectBinding { graph ->
            zoomScrollPane(graph!!, scaleValue = 0.3) {
                style += "-fx-focus-color: transparent;"
                hvalue = (hmin + hmax) / 2
                vvalue = (vmin + vmax) / 2
            }
        })
        leftProperty().bind(graphProperty.objectBinding { graph ->
            tabpane {
                tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
                tab("General") { propertySheet(graph!!.props.propertySheetItems.toObservable()) }
                tabs.add(LayoutTabView(graph!!))
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
        titleProperty.bind(serializationController.openedGraphTitleObservableValue
            .stringBinding { listOfNotNull("Graph analyzer", it).joinToString(" - ") })
    }
}