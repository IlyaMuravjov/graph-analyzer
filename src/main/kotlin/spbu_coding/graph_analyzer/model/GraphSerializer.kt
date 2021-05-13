package spbu_coding.graph_analyzer.model

import javafx.geometry.Point2D
import javafx.scene.paint.Color
import javafx.stage.FileChooser.ExtensionFilter
import java.io.File

interface FileGraphSerializer : GraphSerializer<File> {
    val extensionFilter: ExtensionFilter
}

interface GraphSerializer<in T> {
    fun serialize(output: T, graph: Graph<SerializableVertex>)
    fun deserialize(input: T): Graph<SerializableVertex>
}

data class SerializableVertex(
    val name: String,
    val pos: Point2D = Point2D.ZERO,
    val radius: Double = 7.0,
    val color: Color = Color.LIGHTGREY
)
