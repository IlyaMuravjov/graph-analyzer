package spbu_coding.graph_analyzer.controller.algorithm

import javafx.scene.paint.Color
import spbu_coding.graph_analyzer.model.impl.algorithm.community.CommunityAlgorithmCategory
import spbu_coding.graph_analyzer.view.GraphView
import kotlin.math.pow
import kotlin.random.Random

private val PREDETERMINED_COLORS = listOf<Color>(
    Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CORAL, Color.BROWN, Color.PURPLE,
    Color.DARKGRAY, Color.WHITE, Color.CYAN, Color.BLACK, Color.CHARTREUSE, Color.MAGENTA
)

class CommunityController(private val graphView: GraphView) : GraphAlgorithmCategoryController {
    override val category get() = CommunityAlgorithmCategory
    override val algorithms = category.createAlgorithms(graphView.graph)

    override fun refreshGraph() = Unit

    override fun refreshView() {
        val colorsToAvoid = graphView.vertexViews.mapTo(mutableSetOf()) { it.color }
        val remainingPredeterminedColors = (PREDETERMINED_COLORS - colorsToAvoid).toMutableList()
        val usedColors = mutableSetOf<Color>()
        graphView.vertexViews
            .groupBy { it.vertex.community.id }.values
            .sortedByDescending { it.size }
            .forEach { community ->
                val color = community
                    .groupBy { it.color }.values
                    .filterNot { it.first().color in usedColors }
                    .maxByOrNull { it.size }
                    ?.first()?.color
                    ?: remainingPredeterminedColors.removeLastOrNull()
                    ?: generateSequence { Color.color(Random.nextDouble(), Random.nextDouble(), Random.nextDouble()) }
                        .take(10)
                        .maxByOrNull { color ->
                            colorsToAvoid.minOfOrNull {
                                2 * (color.red - it.red).pow(2) +
                                        4 * (color.green - it.green).pow(2) +
                                        3 * (color.blue - it.blue).pow(2)
                            } ?: 0.0
                        }!!
                usedColors.add(color)
                colorsToAvoid.add(color)
                community.forEach { it.color = color }
            }
    }
}
