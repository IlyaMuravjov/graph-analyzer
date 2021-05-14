package spbu_coding.graph_analyzer.controller.algorithm

import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import org.controlsfx.control.PropertySheet
import spbu_coding.graph_analyzer.model.impl.algorithm.community.CommunityAlgorithmCategory
import spbu_coding.graph_analyzer.utils.CopyablePropertySheetItemsHolder
import spbu_coding.graph_analyzer.view.GraphView
import kotlin.random.Random

private const val COMMUNITY_COLOR_RANDOM_SEED = 1495244474298249277L

class CommunityService(
    graphView: GraphView
) : AbstractGraphAlgorithmCategoryService<CommunityServiceProps>(
    graphView,
    CommunityAlgorithmCategory,
    CommunityServiceProps()
) {
    override fun pausesBetweenIterations() = props.pauseBetweenIterations

    override fun refreshView() {
        val random = Random(COMMUNITY_COLOR_RANDOM_SEED)
        val usedPaints = mutableSetOf<Paint>()
        graphView.vertices
            .groupBy { it.vertex.community.id }.values
            .sortedByDescending { it.size }
            .forEach { community ->
                var paint = community.map { it.circle.fill }.groupBy { it }.values.maxByOrNull { it.size }!!.first()
                if (paint in usedPaints)
                    paint = Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble())
                else usedPaints.add(paint)
                community.forEach { it.circle.fill = paint }
            }
    }
}

data class CommunityServiceProps(
    var pauseBetweenIterations: Boolean = true
) : CopyablePropertySheetItemsHolder<CommunityServiceProps> {
    override val propertySheetItems: List<PropertySheet.Item>
        get() = listOf(beanProperty("pauseBetweenIterations", "Pause between iterations"))

    override fun copyWritableProps() = copy()
}
