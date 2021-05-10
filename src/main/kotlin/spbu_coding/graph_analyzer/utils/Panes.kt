package spbu_coding.graph_analyzer.utils

import javafx.event.EventTarget
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import tornadofx.add
import tornadofx.attachTo
import kotlin.math.exp

fun Pane.subPane(subPane: Pane) {
    add(subPane)
    subPane.minWidthProperty().bind(widthProperty())
    subPane.minHeightProperty().bind(heightProperty())
}

fun Pane.subPane(op: Pane.() -> Unit = {}): Pane = Pane().also { subPane ->
    subPane(subPane)
    subPane.op()
}

fun EventTarget.zoomScrollPane(
    target: Node,
    scaleValue: Double = 1.0,
    zoomIntensity: Double = 1.0,
    op: ZoomScrollPane.() -> Unit
) = ZoomScrollPane(target, scaleValue, zoomIntensity).attachTo(this, op)

private const val ZOOM_INTENSITY_FACTOR = 0.002

class ZoomScrollPane(
    private val target: Node,
    private var scaleValue: Double,
    private val zoomIntensity: Double
) : ScrollPane() {
    private val zoomGroup = Group(target)

    init {
        content = VBox(zoomGroup).apply {
            alignment = Pos.CENTER
            setOnScroll {
                it.consume()
                onScroll(it.deltaY, Point2D(it.x, it.y))
            }
        }
        isPannable = true
        hbarPolicy = ScrollBarPolicy.NEVER
        vbarPolicy = ScrollBarPolicy.NEVER
        isFitToHeight = true
        isFitToWidth = true
        updateScale()
    }

    private fun updateScale() {
        target.scaleX = scaleValue
        target.scaleY = scaleValue
    }

    private fun onScroll(wheelDelta: Double, mousePos: Point2D) {
        val zoomFactor = exp(wheelDelta * ZOOM_INTENSITY_FACTOR * zoomIntensity)
        val innerBounds = zoomGroup.layoutBounds
        val viewportBounds = viewportBounds
        val valX = hvalue * (innerBounds.width - viewportBounds.width)
        val valY = vvalue * (innerBounds.height - viewportBounds.height)
        scaleValue *= zoomFactor
        updateScale()
        layout()
        val posInTarget = target.parentToLocal(zoomGroup.parentToLocal(mousePos))
        val adjustment = target.localToParentTransform.deltaTransform(posInTarget.multiply(zoomFactor - 1))
        val updatedInnerBounds = zoomGroup.boundsInLocal
        hvalue = (valX + adjustment.x) / (updatedInnerBounds.width - viewportBounds.width)
        vvalue = (valY + adjustment.y) / (updatedInnerBounds.height - viewportBounds.height)
    }
}
