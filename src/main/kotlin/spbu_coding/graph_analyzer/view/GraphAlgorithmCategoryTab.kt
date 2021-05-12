package spbu_coding.graph_analyzer.view

import javafx.scene.control.Tab
import javafx.scene.layout.Priority
import spbu_coding.graph_analyzer.controller.algorithm.GraphAlgorithmCategoryService
import spbu_coding.graph_analyzer.utils.propertySheet
import tornadofx.*

class GraphAlgorithmCategoryTab(
    private val service: GraphAlgorithmCategoryService
) : Tab(service.category.displayName) {
    init {
        vbox(ViewConstants.SPACING) {
            padding = ViewConstants.INSETS
            hbox(ViewConstants.SPACING) {
                choicebox(service.algorithmProperty, service.algorithms) {
                    hgrow = Priority.ALWAYS
                    maxWidthProperty().bind(this@hbox.widthProperty())
                    disableWhen(service.toggledObservableValue)
                }
                button {
                    action {
                        service.toggle()
                    }
                    textProperty().bind(service.toggledObservableValue.stringBinding { if (it!!) "Stop" else "Run" })
                }
            }
            propertySheet(service.observablePropertySheetItems)
        }
        service.algorithmProperty.onChange { tabPane.requestLayout() }
    }
}