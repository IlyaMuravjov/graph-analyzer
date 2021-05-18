package spbu_coding.graph_analyzer.model.impl.algorithm.layout

import javafx.geometry.Point2D
import spbu_coding.graph_analyzer.model.Graph
import spbu_coding.graph_analyzer.model.Vertex
import spbu_coding.graph_analyzer.model.VertexLayout
import spbu_coding.graph_analyzer.model.impl.algorithm.AbstractGraphAlgorithm
import spbu_coding.graph_analyzer.model.impl.map
import spbu_coding.graph_analyzer.utils.CopyablePropsHolder
import kotlin.random.Random

const val DEFAULT_MAX_RANDOM_COORDINATE_DEVIATION = 100.0

abstract class AbstractLayoutAlgorithm<out L : VertexLayout, P : CopyablePropsHolder<P>>(
    displayName: String,
    protected val uiGraph: Graph<Vertex>,
    uiProps: P
) : AbstractGraphAlgorithm<L, P>(displayName, uiProps) {
    protected abstract fun getVertexLayout(vertex: Vertex): L

    protected fun layoutGraph(): Graph<L> = uiGraph.map { v -> getVertexLayout(v).also { v.layout = it } }

    protected fun ensureSafePositions(vertices: Collection<VertexLayout>) {
        if (vertices.all { it.pos == Point2D.ZERO }) setRandomPositions(vertices)
    }

    protected fun setRandomPositions(
        vertices: Collection<VertexLayout>,
        maxCoordinateDeviation: Double = DEFAULT_MAX_RANDOM_COORDINATE_DEVIATION
    ) = vertices.forEach {
        it.pos = Point2D(
            Random.nextDouble(-maxCoordinateDeviation, maxCoordinateDeviation),
            Random.nextDouble(-maxCoordinateDeviation, maxCoordinateDeviation)
        )
    }
}
