package spbu_coding.graph_analyzer.model.impl

import javafx.geometry.Point2D
import spbu_coding.graph_analyzer.model.*

fun Graph<Vertex>.copy(): Graph<Vertex> = map { it.copy() }

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

    fun getVertex(key: K): V = vertices.getValue(key)

    fun addEdge(from: K, to: K, weight: Double): Edge<V> =
        EdgeImpl(getVertex(from), getVertex(to), weight).also { edges.add(it) }

    fun build(): Graph<V> = GraphImpl(vertices.values.toList(), edges.toList())
}

data class GraphImpl<out V>(override val vertices: Collection<V>, override val edges: Collection<Edge<V>>) : Graph<V>

data class EdgeImpl<out V>(override val from: V, override val to: V, override val weight: Double) : Edge<V>

fun Vertex.copy(): Vertex = VertexImpl(name, layout.copy(), community.copy(), centrality.copy())

data class VertexImpl(
    override val name: String,
    override var layout: VertexLayout = VertexLayoutImpl(),
    override var community: VertexCommunity = VertexCommunityImpl(),
    override var centrality: VertexCentrality = VertexCentralityImpl()
) : Vertex

fun VertexLayout.copy(): VertexLayout = VertexLayoutImpl(pos, radius)

data class VertexLayoutImpl(override var pos: Point2D = Point2D.ZERO, override var radius: Double = 0.0) : VertexLayout

fun VertexCommunity.copy(): VertexCommunity = VertexCommunityImpl(id)

data class VertexCommunityImpl(override var id: Int? = null) : VertexCommunity

fun VertexCentrality.copy(): VertexCentrality = VertexCentralityImpl(value)

data class VertexCentralityImpl(override var value: Double? = null) : VertexCentrality
