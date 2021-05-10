package spbu_coding.graph_analyzer.model

import javafx.geometry.Point2D

const val MAX_VERTEX_NAME_LENGTH = 50

interface Graph<out V> {
    val vertices: Collection<V>
    val edges: Collection<Edge<V>>
}

interface Edge<out V> {
    val from: V
    val to: V
    val weight: Double
}

interface Vertex {
    val name: String
    var layout: VertexLayout
    var community: VertexCommunity
    var centrality: VertexCentrality
}

interface VertexLayout {
    var pos: Point2D
    var radius: Double
}

interface VertexCommunity {
    var id: Int?
}

interface VertexCentrality {
    var value: Double?
}
