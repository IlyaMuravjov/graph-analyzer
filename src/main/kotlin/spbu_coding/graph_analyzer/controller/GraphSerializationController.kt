package spbu_coding.graph_analyzer.controller

import javafx.beans.value.ObservableValue
import javafx.concurrent.Task
import spbu_coding.graph_analyzer.model.Graph
import spbu_coding.graph_analyzer.model.GraphAnalysisType
import spbu_coding.graph_analyzer.model.GraphSerializer
import spbu_coding.graph_analyzer.model.Vertex
import spbu_coding.graph_analyzer.model.impl.GraphImpl
import spbu_coding.graph_analyzer.model.impl.copy
import spbu_coding.graph_analyzer.model.impl.serialize.fileGraphSerializers
import spbu_coding.graph_analyzer.utils.addOnFail
import spbu_coding.graph_analyzer.utils.addOnSuccess
import spbu_coding.graph_analyzer.utils.runAsyncWithDialog
import tornadofx.*
import java.io.File

class GraphSerializationController(private val view: View) : Controller() {
    private val openedGraphSourceProperty = objectProperty<Any?>(null)
    private var openedGraphSource: Any? by openedGraphSourceProperty

    val openedGraphProperty = objectProperty<Graph<Vertex>>(GraphImpl(emptyList(), emptyList()))
    var openedGraph: Graph<Vertex> by openedGraphProperty

    val openedGraphTitleObservableValue: ObservableValue<String?> =
        openedGraphSourceProperty.objectBinding { it?.toString() }
    val openedGraphTitle: String? by openedGraphTitleObservableValue

    fun openFile(analysisType: GraphAnalysisType) {
        val file = chooseGraphFile("Open $analysisType", FileChooserMode.Single, analysisType) ?: return
        findFileGraphSerializer(file, analysisType).loadAsync(file, analysisType)
    }

    fun saveAsFile(analysisType: GraphAnalysisType) {
        val file = chooseGraphFile("Save $analysisType as", FileChooserMode.Save, analysisType) ?: return
        findFileGraphSerializer(file, analysisType).saveAsync(file, analysisType)
    }

    private fun chooseGraphFile(title: String, mode: FileChooserMode, analysisType: GraphAnalysisType): File? =
        chooseFile(
            title = title,
            filters = fileGraphSerializers(analysisType).map { it.extensionFilter }.toTypedArray(),
            initialDirectory = (openedGraphSource as? File)?.parentFile ?: defaultSaveDirectory(),
            mode = mode,
            owner = view.currentWindow
        ).firstOrNull()

    private fun findFileGraphSerializer(file: File, analysisType: GraphAnalysisType) =
        fileGraphSerializers(analysisType).find { serializer ->
            serializer.extensionFilter.extensions.any { file.path.endsWith(it.drop(1)) }
        } ?: throw IllegalArgumentException("Unknown file extension \"${file.extension}\" for $file")

    private fun <T> GraphSerializer<T>.saveAsync(output: T, analysisType: GraphAnalysisType): Task<Unit> {
        val graphCopy = openedGraph.copy()
        return view.runAsyncWithDialog("Saving $analysisType graph to $output", daemon = false) {
            serialize(output, graphCopy)
        } addOnSuccess {
            openedGraphSource = output
        } addOnFail {
            throw RuntimeException("Unable to save $analysisType graph to $output", it)
        }
    }

    private fun <T> GraphSerializer<T>.loadAsync(input: T, analysisType: GraphAnalysisType): Task<Graph<Vertex>> =
        view.runAsyncWithDialog("Loading $analysisType graph from $input", daemon = true) {
            deserialize(input)
        } addOnSuccess {
            openedGraph = it
            openedGraphSource = input
        } addOnFail {
            throw RuntimeException("Unable to load $analysisType graph from $input", it)
        }

    private fun defaultSaveDirectory() = runCatching {
        File("${System.getProperty("user.home")}/Documents/graph-analyzer").takeIf { it.isDirectory || it.mkdirs() }
    }.getOrNull()
}
