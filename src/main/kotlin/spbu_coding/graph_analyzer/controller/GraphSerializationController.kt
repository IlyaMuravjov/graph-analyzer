package spbu_coding.graph_analyzer.controller

import javafx.beans.property.ReadOnlyProperty
import javafx.concurrent.Task
import spbu_coding.graph_analyzer.model.Graph
import spbu_coding.graph_analyzer.model.GraphSerializer
import spbu_coding.graph_analyzer.model.SerializableVertex
import spbu_coding.graph_analyzer.model.impl.GraphImpl
import spbu_coding.graph_analyzer.model.impl.map
import spbu_coding.graph_analyzer.model.impl.serialize.SerializableVertexImpl
import spbu_coding.graph_analyzer.model.impl.serialize.fileGraphSerializers
import spbu_coding.graph_analyzer.model.impl.serialize.neo4j.Neo4jGraphSerializer
import spbu_coding.graph_analyzer.utils.addOnFail
import spbu_coding.graph_analyzer.utils.addOnSuccess
import spbu_coding.graph_analyzer.utils.runAsyncWithDialog
import spbu_coding.graph_analyzer.utils.toReadOnlyProperty
import spbu_coding.graph_analyzer.view.GraphView
import spbu_coding.graph_analyzer.view.requestNeo4jCredentials
import tornadofx.*
import java.io.File

class GraphSerializationController(private val view: View) : Controller() {
    private val openedGraphSourceProperty = objectProperty<Any?>(null)
    private var openedGraphSource: Any? by openedGraphSourceProperty

    val openedGraphProperty = objectProperty<Graph<SerializableVertex>>(GraphImpl(emptyList(), emptyList()))
    var openedGraph: Graph<SerializableVertex> by openedGraphProperty

    val openedGraphViewProperty = objectProperty<GraphView>()
    var openedGraphView: GraphView by openedGraphViewProperty

    val openedGraphTitleProperty: ReadOnlyProperty<String?> =
        openedGraphSourceProperty.objectBinding { it?.toString() }.toReadOnlyProperty()
    val openedGraphTitle: String? by openedGraphTitleProperty

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
            initialDirectory = (openedGraphSource as? File)?.parentFile ?: defaultGraphDirectory(),
            mode = mode,
            owner = view.currentWindow
        ).firstOrNull()

    private fun defaultGraphDirectory() = runCatching {
        File("${System.getProperty("user.home")}/Documents/graph-analyzer").takeIf { it.isDirectory || it.mkdirs() }
    }.getOrNull()

    private fun findFileGraphSerializer(file: File) =
        fileGraphSerializers().find { serializer ->
            serializer.extensionFilter.extensions.any { file.path.endsWith(it.drop(1)) }
        } ?: throw IllegalArgumentException("Unknown file extension \"${file.extension}\" for $file")

    fun loadFromNeo4j() = requestNeo4jCredentials("Load from Neo4j", view.currentWindow) {
        Neo4jGraphSerializer.loadAsync(it)
    }

    fun saveToNeo4j() = requestNeo4jCredentials("Save to Neo4j", view.currentWindow) {
        Neo4jGraphSerializer.saveAsync(it)
    }

    private fun <T> GraphSerializer<T>.saveAsync(output: T): Task<Unit> {
        val serializableGraph = openedGraphView.viewGraph.map {
            SerializableVertexImpl(it.vertex.id, it.pos, it.radius, it.color)
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
}
