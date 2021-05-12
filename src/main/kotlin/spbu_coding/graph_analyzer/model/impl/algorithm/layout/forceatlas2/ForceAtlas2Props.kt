package spbu_coding.graph_analyzer.model.impl.algorithm.layout.forceatlas2

import spbu_coding.graph_analyzer.model.Graph
import spbu_coding.graph_analyzer.model.Vertex
import spbu_coding.graph_analyzer.utils.CopyablePropertySheetItemsHolder

data class ForceAtlas2Props(
    var tolerance: Double = 1.0,
    var scaling: Double,
    var strongGravity: Boolean = false,
    var gravityCoefficient: Double = 1.0,
    var preventOverlap: Boolean = false,
    var dissuadeHubs: Boolean = false,
    var attractionType: AttractionType = AttractionType.LINEAR,
    var edgeWeightExponent: Double = 1.0,
    var multithreaded: Boolean,
    var barnesHutApproximation: Boolean,
    var barnesHutTheta: Double = 0.85
) : CopyablePropertySheetItemsHolder<ForceAtlas2Props> {
    constructor(graph: Graph<Vertex>) : this(
        scaling = if (graph.vertices.size >= 100) 2.0 else 10.0,
        multithreaded = graph.vertices.size >= 1000,
        barnesHutApproximation = graph.vertices.size >= 1000,
    )

    override val propertySheetItems
        get() = listOf(
            beanProperty("tolerance", "Tolerance"),
            beanProperty("scaling", "Scaling"),
            beanProperty("strongGravity", "Strong gravity"),
            beanProperty("gravityCoefficient", "Gravity"),
            beanProperty("preventOverlap", "Prevent overlap"),
            beanProperty("dissuadeHubs", "Dissuade hubs"),
            beanProperty("attractionType", "Attraction type"),
            beanProperty("edgeWeightExponent", "Edge weight exponent"),
            beanProperty("multithreaded", "Multithreaded"),
            beanProperty("barnesHutApproximation", "Barnes-Hut approximation"),
            beanProperty("barnesHutTheta", "Barnes-Hut theta"),
        )

    override fun copyWritableProps() = copy()
}
