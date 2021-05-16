package spbu_coding.graph_analyzer.view

import javafx.beans.property.ReadOnlyProperty
import javafx.scene.control.TabPane
import javafx.scene.input.KeyCombination
import javafx.scene.layout.Priority
import spbu_coding.graph_analyzer.controller.GraphSerializationController
import spbu_coding.graph_analyzer.controller.algorithm.*
import spbu_coding.graph_analyzer.utils.*
import tornadofx.*

private const val DEFAULT_GRAPH_SCALE = 0.3

class MainView : View() {
    private val serializationController = GraphSerializationController(this)
    private val graphViewProperty = serializationController.openedGraphProperty.nonNullBinding { GraphView(it) }
    private val graphView: GraphView by graphViewProperty
    private val graphZoomScrollPaneProperty: ReadOnlyProperty<ZoomScrollPane> =
        graphViewProperty.nonNullBinding { graphView ->
            zoomScrollPane(graphView, scaleValue = DEFAULT_GRAPH_SCALE) {
                style += "-fx-focus-color: transparent;"
                hvalue = 0.5
                vvalue = 0.5
            }
        }.toReadOnlyProperty()
    private val graphZoomScrollPane: ZoomScrollPane by graphZoomScrollPaneProperty

    override val root = borderpane {
        centerProperty().bind(graphZoomScrollPaneProperty)
        leftProperty().bind(graphViewProperty.nonNullBinding { graphView ->
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
                                    graphZoomScrollPane.run {
                                        hvalue = GRAPH_PANE_CENTER.x / graphView.width
                                        vvalue = GRAPH_PANE_CENTER.y / graphView.height
                                    }
                                }
                            }
                        }
                        propertySheet(graphView.props.propertySheetItems.toObservable())
                    }
                }
                tabs.addAll(listOf(
                    LayoutController(graphView),
                    CommunityController(graphView)
                ).map { GraphAlgorithmCategoryTab(GraphAlgorithmCategoryService(it, graphView)) })
            }
        })
        top = menubar {
            menu("File") {
                item("Open", KeyCombination.valueOf("Shortcut+O")).action { serializationController.openFile() }
                item("Save as", KeyCombination.valueOf("Shortcut+S")).action { serializationController.saveAsFile() }
                item("Load from Neo4j").action { serializationController.loadFromNeo4j() }
                item("Save to Neo4j").action { serializationController.saveToNeo4j() }
            }
        }
    }

    init {
        serializationController.openedGraphViewProperty.bind(graphViewProperty)
        titleProperty.bind(serializationController.openedGraphTitleProperty
            .stringBinding { listOfNotNull("Graph analyzer", it).joinToString(" - ") })
    }
}
