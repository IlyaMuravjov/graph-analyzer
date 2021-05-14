package spbu_coding.graph_analyzer.controller

import javafx.beans.value.ObservableValue
import javafx.concurrent.Task
import javafx.geometry.Pos
import javafx.scene.control.PasswordField
import javafx.scene.paint.Color
import org.controlsfx.control.PropertySheet
import org.controlsfx.property.editor.AbstractPropertyEditor
import org.controlsfx.property.editor.PropertyEditor
import spbu_coding.graph_analyzer.model.Graph
import spbu_coding.graph_analyzer.model.GraphSerializer
import spbu_coding.graph_analyzer.model.SerializableVertex
import spbu_coding.graph_analyzer.model.impl.GraphImpl
import spbu_coding.graph_analyzer.model.impl.map
import spbu_coding.graph_analyzer.model.impl.serialize.fileGraphSerializers
import spbu_coding.graph_analyzer.model.impl.serialize.neo4j.Neo4jConnectionData
import spbu_coding.graph_analyzer.model.impl.serialize.neo4j.Neo4jGraphSerializer
import spbu_coding.graph_analyzer.utils.*
import spbu_coding.graph_analyzer.view.GraphView
import spbu_coding.graph_analyzer.view.ViewConstants
import tornadofx.*
import java.io.File
import java.util.*

class GraphSerializationController(private val view: View) : Controller() {
    private val openedGraphSourceProperty = objectProperty<Any?>(null)
    private var openedGraphSource: Any? by openedGraphSourceProperty

    val openedGraphProperty = objectProperty<Graph<SerializableVertex>>(GraphImpl(emptyList(), emptyList()))
    var openedGraph: Graph<SerializableVertex> by openedGraphProperty

    val openedGraphViewProperty = objectProperty<GraphView>()
    var openedGraphView: GraphView by openedGraphViewProperty

    val openedGraphTitleObservableValue: ObservableValue<String?> =
        openedGraphSourceProperty.objectBinding { it?.toString() }
    val openedGraphTitle: String? by openedGraphTitleObservableValue

    fun openFile() {
        val file = chooseGraphFile("Open", FileChooserMode.Single) ?: return
        findFileGraphSerializer(file).loadAsync(file)
    }

    fun saveAsFile() {
        val file = chooseGraphFile("Save as", FileChooserMode.Save) ?: return
        findFileGraphSerializer(file).saveAsync(file)
    }

    private fun chooseGraphFile(title: String, mode: FileChooserMode): File? =
        chooseFile(
            title = title,
            filters = fileGraphSerializers().map { it.extensionFilter }.toTypedArray(),
            initialDirectory = (openedGraphSource as? File)?.parentFile ?: defaultSaveDirectory(),
            mode = mode,
            owner = view.currentWindow
        ).firstOrNull()

    fun loadFromNeo4j() = chooseNeo4jConnectionData("Load from Neo4j") {
        Neo4jGraphSerializer.loadAsync(it)
    }

    fun saveToNeo4j() = chooseNeo4jConnectionData("Save to Neo4j") {
        Neo4jGraphSerializer.saveAsync(it)
    }

    private fun chooseNeo4jConnectionData(title: String, op: (Neo4jConnectionData) -> Unit) {
        val props = Neo4jConnectionProps()
        view.builderWindow(title = title) {
            vbox(ViewConstants.SPACING) {
                propertySheet(props.propertySheetItems.toObservable())
                hbox(ViewConstants.SPACING) {
                    padding = ViewConstants.INSETS.copy(top = 0.0)
                    alignment = Pos.CENTER
                    button("  OK  ") {
                        action {
                            this@builderWindow.close()
                            op(Neo4jConnectionData(props.uri, props.username, props.password))
                        }
                    }
                    button("Cancel") {
                        action { this@builderWindow.close() }
                    }
                }
            }
        }
    }

    private fun findFileGraphSerializer(file: File) =
        fileGraphSerializers().find { serializer ->
            serializer.extensionFilter.extensions.any { file.path.endsWith(it.drop(1)) }
        } ?: throw IllegalArgumentException("Unknown file extension \"${file.extension}\" for $file")

    private fun <T> GraphSerializer<T>.saveAsync(output: T): Task<Unit> {
        val serializableGraph = openedGraphView.viewGraph.map {
            SerializableVertex(it.vertex.name, it.pos, it.circle.radius, it.circle.fill as Color)
        }
        return view.runAsyncWithDialog("Saving graph to $output", daemon = false) {
            serialize(output, serializableGraph)
        } addOnSuccess {
            openedGraphSource = output
        } addOnFail {
            throw RuntimeException("Unable to save graph to $output", it)
        }
    }

    private fun <T> GraphSerializer<T>.loadAsync(input: T): Task<Graph<SerializableVertex>> =
        view.runAsyncWithDialog("Loading graph from $input", daemon = true) {
            deserialize(input)
        } addOnSuccess {
            openedGraph = it
            openedGraphSource = input
        } addOnFail {
            throw RuntimeException("Unable to load graph from $input", it)
        }

    private fun defaultSaveDirectory() = runCatching {
        File("${System.getProperty("user.home")}/Documents/graph-analyzer").takeIf { it.isDirectory || it.mkdirs() }
    }.getOrNull()
}

data class Neo4jConnectionProps(
    var uri: String = "",
    var username: String = "",
    var password: String = ""
) : PropertySheetItemsHolder {
    override val propertySheetItems = listOf(
        beanProperty("uri", "URI"),
        beanProperty("username", "Username"),
        beanProperty("password", "Password").let {
            object : PropertySheet.Item by it {
                override fun getPropertyEditorClass(): Optional<Class<out PropertyEditor<*>>> =
                    Optional.of(PasswordEditor::class.java)
            }
        }
    )
}

class PasswordEditor(
    item: PropertySheet.Item
) : AbstractPropertyEditor<String, PasswordField>(item, PasswordField()) {
    override fun setValue(value: String?) {
        editor.text = value.toString()
    }

    override fun getObservableValue(): ObservableValue<String> = editor.textProperty()
}
