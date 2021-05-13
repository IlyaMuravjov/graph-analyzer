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
import spbu_coding.graph_analyzer.model.Edge
import spbu_coding.graph_analyzer.model.Graph
import spbu_coding.graph_analyzer.model.SerializableVertex
import spbu_coding.graph_analyzer.model.Vertex
import spbu_coding.graph_analyzer.model.impl.VertexImpl
import spbu_coding.graph_analyzer.model.impl.buildGraph
import spbu_coding.graph_analyzer.model.impl.map
import spbu_coding.graph_analyzer.utils.PropertySheetItemsHolder
import spbu_coding.graph_analyzer.utils.subPane
import tornadofx.*

class GraphView(
    serializableGraph: Graph<SerializableVertex>,
) : Pane() {
    private val size = Point2D(100_000.0, 100_000.0)
    private val center = size / 2.0
    val props = GraphViewProps(serializableGraph.vertices.size, serializableGraph.edges.size)
    var viewGraph = serializableGraph.map { VertexView(it, props, center) }
    val vertices get() = viewGraph.vertices
    val edges = mutableListOf<EdgeView>()
    val graph = buildGraph<VertexView, Vertex> {
        viewGraph.vertices.forEach { addVertex(it, it.vertex) }
        viewGraph.edges.forEach { edges.add(EdgeView(addEdge(it.from, it.to, it.weight), it.from, it.to, props)) }
    }
    var vertexPane: Pane

    init {
        minWidth = size.x
        minHeight = size.y
        EdgeRenderingController(subPane(), edges).apply {
            props.edgesRenderedProperty.bind(edgesRenderedObservableValue)
            prefEdgesRenderedProperty.bind(props.prefEdgesRenderedProperty)
        }
        vertexPane = subPane { children.addAll(vertices) }
        subPane {
            visibleWhen(props.vertexLabelsVisibleProperty)
            children.addAll(vertices.map { it.label })
        }
    }
}

class EdgePane(edges: List<EdgeView>) : Pane() {
    private val lines = edges.map { it.line() }.also { children.addAll(it) }
    val edgeCount get() = lines.size

    fun render() {
        isVisible = true
        lines.forEach { it.bindProperties() }
    }

    fun unrender() {
        isVisible = false
        lines.forEach { it.unbindProperties() }
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

        fun bindProperties() {
            startXProperty().bind(from.circle.centerXProperty())
            startYProperty().bind(from.circle.centerYProperty())
            endXProperty().bind(to.circle.centerXProperty())
            endYProperty().bind(to.circle.centerYProperty())
        }

        fun unbindProperties() {
            startXProperty().unbind()
            startYProperty().unbind()
            endXProperty().unbind()
            endYProperty().unbind()
        }
    }
}

class VertexView(
    serializableVertex: SerializableVertex,
    val props: GraphViewProps,
    val graphCenter: Point2D
) : Group() {
    val vertex = VertexImpl(serializableVertex.name)
    val circle = Circle(
        serializableVertex.pos.x + graphCenter.x,
        serializableVertex.pos.y + graphCenter.y,
        serializableVertex.radius,
        serializableVertex.color
    )

    var lastDraggedMillis = 0L
    var pos: Point2D
        get() = Point2D(circle.centerX, circle.centerY) - graphCenter
        set(value) {
            circle.centerX = value.x + graphCenter.x
            circle.centerY = value.y + graphCenter.y
        }

    val outline: Shape = Circle().also {
        it.fill = Color.BLACK
        it.centerXProperty().bind(circle.centerXProperty())
        it.centerYProperty().bind(circle.centerYProperty())
        it.radiusProperty().bind(circle.radiusProperty() + 2.0)
    }

    init {
        add(outline)
        add(circle)
    }

    val label = Text(vertex.name).apply {
        fontProperty().bind(circle.radiusProperty().objectBinding { Font(2.0 * it!!.toDouble()) })
        xProperty().bind(circle.centerXProperty() - layoutBounds.width / 2)
        yProperty().bind(circle.centerYProperty() + circle.radiusProperty() + layoutBounds.height)
    }
}

class GraphViewProps(
    val vertexCount: Int,
    val edgeCount: Int
) : PropertySheetItemsHolder {
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
