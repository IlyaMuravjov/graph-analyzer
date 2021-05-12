package spbu_coding.graph_analyzer.controller.algorithm

import javafx.beans.property.Property
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import org.controlsfx.control.PropertySheet
import spbu_coding.graph_analyzer.model.GraphAlgorithm
import spbu_coding.graph_analyzer.model.GraphAlgorithmCategory


interface GraphAlgorithmCategoryService {
    val category: GraphAlgorithmCategory
    val algorithms: List<GraphAlgorithm>
    val algorithmProperty: Property<GraphAlgorithm>
    val algorithm: GraphAlgorithm
    val toggledObservableValue: ObservableValue<Boolean>
    val toggled: Boolean
    val observablePropertySheetItems: ObservableList<PropertySheet.Item>

    fun toggle()
}
