package spbu_coding.graph_analyzer.view

import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import javafx.scene.shape.Shape
import javafx.scene.text.Font
import javafx.scene.text.Text
import org.controlsfx.control.PropertySheet
import spbu_coding.graph_analyzer.controller.EdgeRenderingController
import spbu_coding.graph_analyzer.controller.VertexMouseDragController
import spbu_coding.graph_analyzer.model.Edge
import spbu_coding.graph_analyzer.model.Graph
import spbu_coding.graph_analyzer.model.SerializableVertex
import spbu_coding.graph_analyzer.model.Vertex
import spbu_coding.graph_analyzer.model.impl.VertexImpl
import spbu_coding.graph_analyzer.model.impl.buildGraph
import spbu_coding.graph_analyzer.model.impl.map
import spbu_coding.graph_analyzer.utils.PropsHolder
import spbu_coding.graph_analyzer.utils.nonNullBinding
import spbu_coding.graph_analyzer.utils.subPane
import tornadofx.*

val GRAPH_PANE_INIT_SIZE = Point2D(100_000.0, 100_000.0)
val GRAPH_PANE_CENTER = GRAPH_PANE_INIT_SIZE / 2.0

class GraphView(serializableGraph: Graph<SerializableVertex>) : Pane() {
    val props = GraphViewProps(serializableGraph.vertices.size, serializableGraph.edges.size)
    var viewGraph = serializableGraph.map { VertexView(it, props) }
    val vertexViews get() = viewGraph.vertices
    val edgeViews = mutableListOf<EdgeView>()
    val graph = buildGraph<VertexView, Vertex> {
        viewGraph.vertices.forEach { addVertex(it, it.vertex) }
        viewGraph.edges.forEach {
            val edge = addEdge(it.from, it.to, it.weight)
            edgeViews.add(EdgeView(edge, it.from, it.to, props))
        }
    }
    var vertexPane: Pane

    init {
        minWidth = GRAPH_PANE_INIT_SIZE.x
        minHeight = GRAPH_PANE_INIT_SIZE.y
        EdgeRenderingController(subPane(), edgeViews).apply {
            props.edgesRenderedProperty.bind(edgesRenderedProperty)
            prefEdgesRenderedProperty.bind(props.prefEdgesRenderedProperty)
        }
        vertexPane = subPane { children.addAll(vertexViews) }
        VertexMouseDragController().apply { vertexViews.forEach { registerVertex(it) } }
        subPane {
            visibleWhen(props.vertexLabelsVisibleProperty)
            children.addAll(vertexViews.map { it.label })
        }
    }
}

class EdgeView(
    val edge: Edge<Vertex>,
    val from: VertexView,
    val to: VertexView,
    val props: GraphViewProps
) {
    fun line() = EdgeLine()

    inner class EdgeLine : Line() {
        init {
            strokeWidth = edge.weight
            strokeProperty().bind(props.edgeColorProperty)
        }

        fun bindPosition() {
            startXProperty().bind(from.circle.centerXProperty())
            startYProperty().bind(from.circle.centerYProperty())
            endXProperty().bind(to.circle.centerXProperty())
            endYProperty().bind(to.circle.centerYProperty())
        }

        fun unbindPosition() {
            startXProperty().unbind()
            startYProperty().unbind()
            endXProperty().unbind()
            endYProperty().unbind()
        }
    }
}

class VertexView(
    serializableVertex: SerializableVertex,
    val props: GraphViewProps
) : Group() {
    val vertex = VertexImpl(serializableVertex.id)
    val circle = Circle(
        serializableVertex.pos.x + GRAPH_PANE_CENTER.x,
        serializableVertex.pos.y + GRAPH_PANE_CENTER.y,
        serializableVertex.radius,
        serializableVertex.color
    )

    // Long.MAX_VALUE if vertex is currently being dragged
    var lastMouseReleasedMillis = 0L
    var pos: Point2D
        get() = Point2D(circle.centerX, circle.centerY) - GRAPH_PANE_CENTER
        set(value) {
            circle.centerX = value.x + GRAPH_PANE_CENTER.x
            circle.centerY = value.y + GRAPH_PANE_CENTER.y
        }
    var color: Color
        get() = circle.fill as Color
        set(value) = run { circle.fill = value }
    var radius: Double
        get() = circle.radius
        set(value) = run { circle.radius = value }

    val outline: Shape = Circle().also {
        it.fill = Color.BLACK
        it.centerXProperty().bind(circle.centerXProperty())
        it.centerYProperty().bind(circle.centerYProperty())
        it.radiusProperty().bind(circle.radiusProperty() + 1.0)
    }

    init {
        add(outline)
        add(circle)
    }

    val label = Text(vertex.id).apply {
        fontProperty().bind(circle.radiusProperty().nonNullBinding { Font(2.0 * it.toDouble()) })
        xProperty().bind(circle.centerXProperty() - layoutBounds.width / 2)
        yProperty().bind(circle.centerYProperty() + circle.radiusProperty() + layoutBounds.height)
        isMouseTransparent = true
    }
}

class GraphViewProps(
    val vertexCount: Int,
    val edgeCount: Int
) : PropsHolder {
    val vertexLabelsVisibleProperty = booleanProperty(false)
    var vertexLabelsVisible by vertexLabelsVisibleProperty

    val edgeColorProperty = objectProperty(Color.GRAY)
    var edgeColor: Color by edgeColorProperty

    val prefEdgesRenderedProperty = longProperty(10_000L)
    var prefEdgesRendered by prefEdgesRenderedProperty

    val edgesRenderedProperty = longProperty(0L)
    fun edgesRenderedProperty() = edgesRenderedProperty
    var edgesRendered by edgesRenderedProperty

    override val propertySheetItems: List<PropertySheet.Item>
        get() = listOf(
            beanProperty("vertexCount", "Vertices", readOnly = true),
            beanProperty("edgeCount", "Edges", readOnly = true),
            beanProperty("vertexLabelsVisible", "Vertex labels"),
            beanProperty("edgeColor", "Edge color"),
            beanProperty("prefEdgesRendered", "Preferred edges rendered"),
            beanProperty("edgesRendered", "Edges rendered", readOnly = true)
        )
}
