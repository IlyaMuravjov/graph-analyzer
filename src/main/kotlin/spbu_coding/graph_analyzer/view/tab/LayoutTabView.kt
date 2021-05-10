package spbu_coding.graph_analyzer.view.tab

import javafx.scene.control.Tab
import spbu_coding.graph_analyzer.controller.layout.LayoutController
import spbu_coding.graph_analyzer.model.impl.layout.LayoutAlgorithmType
import spbu_coding.graph_analyzer.utils.propertySheet
import spbu_coding.graph_analyzer.view.Constants.INSETS
import spbu_coding.graph_analyzer.view.Constants.SPACING
import spbu_coding.graph_analyzer.view.GraphView
import tornadofx.*

class LayoutTabView(graph: GraphView) : Tab("Layout") {
    private val controller = LayoutController(graph)

    init {
        vbox(SPACING) {
            padding = INSETS
            hbox(SPACING) {
                val algoChoiceBox = choicebox(controller.algorithmTypeProperty, LayoutAlgorithmType.values().toList()) {
                    disableWhen(controller.toggledObservableValue)
                }
                val toggleButton = button {
                    textProperty().bind(controller.toggledObservableValue.stringBinding { if (it!!) "Stop" else "Run" })
                    action { controller.toggle() }
                }
                algoChoiceBox.minWidthProperty()
                    .bind(this@vbox.widthProperty() - toggleButton.widthProperty() - 5 * SPACING)
            }
            propertySheet(controller.propertySheetItems)
        }
        controller.algorithmTypeProperty.onChange { tabPane.requestLayout() }
    }
}
