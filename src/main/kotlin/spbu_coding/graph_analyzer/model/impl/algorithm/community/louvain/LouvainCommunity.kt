package spbu_coding.graph_analyzer.model.impl.algorithm.community.louvain

import spbu_coding.graph_analyzer.model.VertexCommunity
import kotlin.math.abs
import kotlin.math.pow

class LouvainCommunity(private val _id: Int) : VertexCommunity {
    var superCommunity: LouvainCommunity? = null
    override val id: Int get() = superCommunity?.id ?: _id
    var selfLoopWeight: Double = 0.0
    var edges = mutableMapOf<LouvainCommunity, Double>()
    var outgoingEdgesWeightSum = 0.0

    fun connectTo(other: LouvainCommunity, weight: Double) {
        if (other == this) selfLoopWeight += weight
        else outgoingEdgesWeightSum += weight
        edges[other] = weightToVertex(other) + weight
    }

    fun condensateEdges() {
        val oldEdges = edges
        edges = mutableMapOf()
        oldEdges.forEach { (v, w) -> edges[v.superCommunity!!] = weightToVertex(v.superCommunity!!) + w }
        edges[this] = selfLoopWeight
    }

    fun computeModularityGainOnInsertion(vertex: LouvainCommunity, m: Double): Double {
        val sin = selfLoopWeight
        val sout = selfLoopWeight + outgoingEdgesWeightSum
        val ki = vertex.selfLoopWeight + vertex.outgoingEdgesWeightSum
        val kiin = weightToVertex(vertex)
        val m2 = 2 * m
        return ((sin + kiin) / m2 - ((sout + ki) / m2).pow(2)) -
                ((sin) / m2 - (sout / m2).pow(2) - (ki / m2).pow(2))
    }

    fun computeModularityGainOnRemoval(vertex: LouvainCommunity, m: Double): Double {
        val ki = vertex.outgoingEdgesWeightSum
        val vertexSelfLoopWeight = vertex.selfLoopWeight
        val kiin = weightToVertex(vertex) - vertexSelfLoopWeight
        val sin = selfLoopWeight - kiin - vertexSelfLoopWeight
        val sout = selfLoopWeight + outgoingEdgesWeightSum + kiin - ki
        val dq = ((sin + kiin) / (2 * m) - ((sout + ki) / (2 * m)).pow(2.0)) -
                ((sin) / (2 * m) - (sout / (2 * m)).pow(2.0) - (ki / (2 * m)).pow(2.0))
        return dq
    }

    fun computeModularityGainOnRemoval(totalEdgeWeight: Double) =
        superCommunity!!.computeModularityGainOnRemoval(this, totalEdgeWeight)

    fun insertSubCommunity(vertex: LouvainCommunity) {
        vertex.superCommunity = this
        val toVertexWeight = weightToVertex(vertex)
        val vertexSelfLoopWeight = vertex.selfLoopWeight
        selfLoopWeight += toVertexWeight + vertexSelfLoopWeight
        outgoingEdgesWeightSum -= toVertexWeight
        outgoingEdgesWeightSum += vertex.edges.filter { (to, _) -> to.superCommunity !== this }.values.sum()
        vertex.edges.forEach { (to, weight) -> edges[to] = weightToVertex(to) + weight }
    }

    fun removeVertex(vertex: LouvainCommunity) {
        val vertexSelfLoopWeight = vertex.selfLoopWeight
        val toVertex = weightToVertex(vertex) - vertexSelfLoopWeight
        selfLoopWeight -= toVertex + vertexSelfLoopWeight
        outgoingEdgesWeightSum += toVertex
        outgoingEdgesWeightSum -= vertex.edges.filter { (to, _) -> to.superCommunity !== this }.values.sum()
        vertex.edges.forEach { (to, weight) ->
            edges[to] = weightToVertex(to) - weight
        }
        vertex.superCommunity = null
    }

    fun wrapInSuperCommunity(id: Int) {
        superCommunity = LouvainCommunity(id).also {
            it.selfLoopWeight = selfLoopWeight
            it.edges = edges.toMutableMap().apply { put(this@LouvainCommunity, selfLoopWeight) }
            it.outgoingEdgesWeightSum = outgoingEdgesWeightSum
        }
    }

    fun weightToVertex(vertex: LouvainCommunity) = edges[vertex] ?: 0.0
}
