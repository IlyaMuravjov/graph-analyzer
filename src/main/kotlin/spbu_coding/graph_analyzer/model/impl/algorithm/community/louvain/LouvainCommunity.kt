package spbu_coding.graph_analyzer.model.impl.algorithm.community.louvain

import spbu_coding.graph_analyzer.model.VertexCommunity
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
    }

    fun computeModularityGainOnInsertion(vertex: LouvainCommunity, totalEdgeWeight: Double): Double {
        val toVertexWeight = weightToVertex(vertex)
        val vertexOutgoingWeightSum = vertex.outgoingEdgesWeightSum
        val communitySelfLoopWeight = selfLoopWeight
        val outgoingEdgesWeightSum = outgoingEdgesWeightSum
        return (communitySelfLoopWeight + toVertexWeight) / (2 * totalEdgeWeight) -
                ((outgoingEdgesWeightSum + communitySelfLoopWeight + vertexOutgoingWeightSum) /
                        (2 * totalEdgeWeight)).pow(2.0) -
                ((communitySelfLoopWeight) / (2 * totalEdgeWeight) - ((outgoingEdgesWeightSum + communitySelfLoopWeight)
                        / (2 * totalEdgeWeight)).pow(2.0) -
                        ((vertexOutgoingWeightSum) / (2 * totalEdgeWeight)).pow(2.0))

    }

    fun computeModularityGainOnRemoval(vertex: LouvainCommunity, totalEdgeWeight: Double): Double {
        val toVertexWeight = weightToVertex(vertex)
        val vertexSelfLoopWeight = vertex.selfLoopWeight
        val communitySelfLoopWeight = selfLoopWeight - toVertexWeight - vertexSelfLoopWeight
        val outgoingEdgesWeightSum = outgoingEdgesWeightSum + toVertexWeight
        return (communitySelfLoopWeight + toVertexWeight) / (2 * totalEdgeWeight) -
                ((outgoingEdgesWeightSum + communitySelfLoopWeight + vertexSelfLoopWeight) /
                        (2 * totalEdgeWeight)).pow(2.0) -
                ((communitySelfLoopWeight) / (2 * totalEdgeWeight) - ((outgoingEdgesWeightSum + communitySelfLoopWeight)
                        / (2 * totalEdgeWeight)).pow(2.0) -
                        ((vertexSelfLoopWeight) / (2 * totalEdgeWeight)).pow(2.0))
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
        vertex.superCommunity = null
        val toVertex = weightToVertex(vertex)
        val vertexSelfLoopWeight = vertex.selfLoopWeight
        selfLoopWeight -= toVertex + vertexSelfLoopWeight
        outgoingEdgesWeightSum += toVertex
        outgoingEdgesWeightSum -= vertex.edges.filter { (to, _) -> to.superCommunity !== this }.values.sum()
        vertex.edges.forEach { (to, weight) ->
            edges[to] = weightToVertex(to) - weight
        }
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
