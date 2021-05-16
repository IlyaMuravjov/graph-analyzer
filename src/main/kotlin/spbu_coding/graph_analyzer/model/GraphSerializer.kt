package spbu_coding.graph_analyzer.model

import javafx.geometry.Point2D
import javafx.scene.paint.Color
import javafx.stage.FileChooser.ExtensionFilter
import java.io.File

interface GraphSerializer<in T> {
    fun serialize(output: T, graph: Graph<SerializableVertex>)
    fun deserialize(input: T): Graph<SerializableVertex>
}

interface FileGraphSerializer : GraphSerializer<File> {
    val extensionFilter: ExtensionFilter
}

interface SerializableVertex {
    val id: String
    val pos: Point2D
    val radius: Double
    val color: Color
}
