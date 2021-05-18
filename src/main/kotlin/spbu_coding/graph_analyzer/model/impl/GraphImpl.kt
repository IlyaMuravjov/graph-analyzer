package spbu_coding.graph_analyzer.model.impl

import javafx.geometry.Point2D
import spbu_coding.graph_analyzer.model.*

data class GraphImpl<out V>(override val vertices: Collection<V>, override val edges: Collection<Edge<V>>) : Graph<V> {
    override val lastModifiedMillis: Long = System.currentTimeMillis()
}

inline fun <T, R> Graph<T>.map(mapper: (T) -> R): Graph<R> = buildGraph<T, R> {
    vertices.forEach { addVertex(it, mapper(it)) }
    edges.forEach { addEdge(it.from, it.to, it.weight) }
}

inline fun <K, V> buildGraph(init: GraphBuilder<K, V>.() -> Unit) = GraphBuilder<K, V>().apply(init).build()

class GraphBuilder<in K, V> {
    private val vertices = mutableMapOf<K, V>()
    private val edges = mutableListOf<Edge<V>>()

    fun addVertex(key: K, vertex: V) {
        vertices[key] = vertex
    }

    fun addEdge(from: K, to: K, weight: Double): Edge<V> =
        EdgeImpl(vertices.getValue(from), vertices.getValue(to), weight).also { edges.add(it) }

    fun build(): Graph<V> = GraphImpl(vertices.values.toList(), edges.toList())
}

data class EdgeImpl<out V>(override val from: V, override val to: V, override val weight: Double) : Edge<V>

data class VertexImpl(
    override val id: String,
    override var layout: VertexLayout = VertexLayoutImpl(),
    override var community: VertexCommunity = VertexCommunityImpl(),
    override var centrality: VertexCentrality = VertexCentralityImpl()
) : Vertex

data class VertexLayoutImpl(override var pos: Point2D = Point2D.ZERO, override var radius: Double = 0.0) : VertexLayout

data class VertexCommunityImpl(override var id: Int? = null) : VertexCommunity

data class VertexCentralityImpl(override var value: Double? = null) : VertexCentrality
