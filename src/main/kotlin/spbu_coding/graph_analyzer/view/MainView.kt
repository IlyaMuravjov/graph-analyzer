package spbu_coding.graph_analyzer.view

import javafx.scene.control.TabPane
import javafx.scene.input.KeyCombination
import javafx.scene.layout.Priority
import spbu_coding.graph_analyzer.controller.GraphSerializationController
import spbu_coding.graph_analyzer.controller.algorithm.CommunityService
import spbu_coding.graph_analyzer.controller.algorithm.LayoutService
import spbu_coding.graph_analyzer.utils.propertySheet
import spbu_coding.graph_analyzer.utils.zoomScrollPane
import tornadofx.*

private const val DEFAULT_GRAPH_SCALE = 0.3

class MainView : View() {
    private val serializationController = GraphSerializationController(this)
    private val graphViewProperty = serializationController.openedGraphProperty.objectBinding { GraphView(it!!) }
    private val graphView by graphViewProperty
    private val graphZoomScrollPaneObservableValue = graphViewProperty.objectBinding { graphView ->
        zoomScrollPane(graphView!!, scaleValue = DEFAULT_GRAPH_SCALE) {
            style += "-fx-focus-color: transparent;"
            hvalue = 0.5
            vvalue = 0.5
        }
    }
    private val graphZoomScrollPane by graphZoomScrollPaneObservableValue

    override val root = borderpane {
        centerProperty().bind(graphZoomScrollPaneObservableValue)
        leftProperty().bind(graphViewProperty.objectBinding { graphView ->
            tabpane {
                tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
                tab("General") {
                    vbox {
                        hbox(ViewConstants.SPACING) {
                            padding = ViewConstants.INSETS
                            button("Reset camera position") {
                                hgrow = Priority.ALWAYS
                                maxWidthProperty().bind(this@hbox.widthProperty())
                                action {
                                    graphZoomScrollPane!!.run {
                                        hvalue = graphView!!.center.x / graphView.width
                                        vvalue = graphView.center.y / graphView.height
                                    }
                                }
                            }
                        }
                        propertySheet(graphView!!.props.propertySheetItems.toObservable())
                    }
                }
                tabs.add(GraphAlgorithmCategoryTab(LayoutService(graphView!!)))
                tabs.add(GraphAlgorithmCategoryTab(CommunityService(graphView)))
            }
        })
        top = menubar {
            menu("File") {
                item("Open", KeyCombination.valueOf("Shortcut+O")) {
                    action { serializationController.openFile() }
                }
                item("Save as", KeyCombination.valueOf("Shortcut+S")) {
                    action { serializationController.saveAsFile() }
                }
                item("Load from Neo4j") {
                    action { serializationController.loadFromNeo4j() }
                }
                item("Save to Neo4j") {
                    action { serializationController.saveToNeo4j() }
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
