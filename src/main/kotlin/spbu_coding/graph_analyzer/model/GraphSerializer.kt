package spbu_coding.graph_analyzer.model

import javafx.stage.FileChooser.ExtensionFilter
import java.io.File

interface FileGraphSerializer : GraphSerializer<File> {
    val extensionFilter: ExtensionFilter
}

interface GraphSerializer<in T> {
    fun serialize(output: T, graph: Graph<Vertex>)
    fun deserialize(input: T): Graph<Vertex>
}
