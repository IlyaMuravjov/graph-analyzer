package spbu_coding.graph_analyzer.model.impl.algorithm.layout.forceatlas2

import javafx.geometry.Point2D
import spbu_coding.graph_analyzer.model.Edge
import spbu_coding.graph_analyzer.model.VertexLayout
import tornadofx.Vector2D
import tornadofx.plus

typealias ForceAtlas2Edge = Edge<ForceAtlas2Vertex>

class ForceAtlas2Vertex(
    override var pos: Point2D,
    override var radius: Double,
    var mass: Double = 1.0,
    var velocity: Vector2D = Vector2D.ZERO,
    var oldVelocity: Vector2D = Vector2D.ZERO
) : VertexLayout {
    constructor(vertexLayout: VertexLayout) : this(vertexLayout.pos, vertexLayout.radius)

    val swinging get() = mass * oldVelocity.distance(velocity)
    val effectiveTraction get() = mass * 0.5 * (oldVelocity + velocity).magnitude()
}
