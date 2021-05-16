package spbu_coding.graph_analyzer.model.impl.serialize

import javafx.geometry.Point2D
import javafx.scene.paint.Color
import spbu_coding.graph_analyzer.model.SerializableVertex

data class SerializableVertexImpl(
    override val id: String,
    override val pos: Point2D,
    override val radius: Double,
    override val color: Color
) : SerializableVertex
