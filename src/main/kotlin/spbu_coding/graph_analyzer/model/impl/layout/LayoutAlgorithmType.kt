package spbu_coding.graph_analyzer.model.impl.layout

import spbu_coding.graph_analyzer.model.Graph
import spbu_coding.graph_analyzer.model.LayoutAlgorithmFactory
import spbu_coding.graph_analyzer.model.Vertex
import spbu_coding.graph_analyzer.model.impl.layout.forceatlas2.ForceAtlas2
import spbu_coding.graph_analyzer.model.impl.layout.forceatlas2.ForceAtlas2Props
import spbu_coding.graph_analyzer.model.impl.layout.random.RandomLayoutAlgorithm
import spbu_coding.graph_analyzer.model.impl.layout.random.RandomLayoutProps
import spbu_coding.graph_analyzer.utils.sharingPropsFactory

enum class LayoutAlgorithmType(val displayName: String) {
    FORCE_ATLAS_2("ForceAtlas 2") {
        override fun createFactory(graph: Graph<Vertex>): LayoutAlgorithmFactory =
            sharingPropsFactory(ForceAtlas2Props(graph)) { ForceAtlas2(graph, props) }
    },
    RANDOM("Random") {
        override fun createFactory(graph: Graph<Vertex>): LayoutAlgorithmFactory =
            sharingPropsFactory(RandomLayoutProps()) { RandomLayoutAlgorithm(graph, props) }
    };

    abstract fun createFactory(graph: Graph<Vertex>): LayoutAlgorithmFactory

    override fun toString() = displayName
}
