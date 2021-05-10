package spbu_coding.graph_analyzer.controller.layout

import javafx.concurrent.ScheduledService
import javafx.concurrent.Task
import spbu_coding.graph_analyzer.model.LayoutAlgorithm
import spbu_coding.graph_analyzer.utils.RefreshablePropsHolder
import spbu_coding.graph_analyzer.utils.RefreshablePropsHolderImpl
import spbu_coding.graph_analyzer.view.GraphView
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.locks.Lock
import kotlin.concurrent.withLock

class LayoutService(
    private val graph: GraphView,
    private val layoutAlgorithm: LayoutAlgorithm<*, *>,
    private val layoutLock: Lock,
    private val uiProps: LayoutServiceProps
) : ScheduledService<Unit>(), RefreshablePropsHolder<LayoutServiceProps> by RefreshablePropsHolderImpl(uiProps) {
    private val lastSecondIterationsMillis: Queue<Long> = ArrayDeque()

    // `true` if there is another running `LayoutService` that is being cancelled and we need to complete one
    // `scheduled() -> createTask().call() -> succeeded()` cycle without actually doing anything
    // so we can wait for layoutLock in `createTask().call()` on a background thread
    // letting another running `LayoutService` to complete cancellation without us waiting for it on the UI thread
    private var awaitsLayoutLock = false

    private var lastFrameMillis = 0L

    // `false` if `layoutAlgorithm`'s graph copy shouldn't be synchronised with UI graph and only props should be updated
    private var uiUpdateRequested = true

    init {
        executor = Executors.newSingleThreadExecutor {
            Thread(it).apply {
                isDaemon = true
                name = "LayoutServiceThread-$name"
            }
        }
    }

    override fun start() {
        if (layoutLock.tryLock()) {
            try {
                super.start()
            } finally {
                layoutLock.unlock()
            }
        } else {
            awaitsLayoutLock = true
            super.start()
        }
    }

    override fun scheduled() {
        if (!awaitsLayoutLock) {
            if (uiUpdateRequested) {
                if (lastFrameMillis == 0L) lastFrameMillis = System.currentTimeMillis()
                layoutLock.withLock {
                    graph.vertices.forEach {
                        it.vertex.layout.pos = it.pos
                        it.vertex.layout.radius = it.radius
                    }
                    layoutAlgorithm.refreshGraph()
                }
            }
            refreshProps()
            layoutAlgorithm.refreshProps()
        }
        super.scheduled()
    }

    override fun createTask(): Task<Unit> = object : Task<Unit>() {
        override fun call() = layoutLock.withLock {
            if (awaitsLayoutLock) return@call
            do {
                if (isCancelled) return@call
                lastSecondIterationsMillis.add(System.currentTimeMillis())
                layoutAlgorithm.runIteration()
            } while (System.currentTimeMillis() - lastFrameMillis < props.minMillisPerPropsUpdate)
            uiUpdateRequested = System.currentTimeMillis() - lastFrameMillis >= props.minMillisPerUiUpdate
        }
    }

    override fun succeeded() {
        if (awaitsLayoutLock) {
            awaitsLayoutLock = false
            return
        }
        if (uiUpdateRequested) layoutLock.withLock {
            val previousFrameMillis = lastFrameMillis
            lastFrameMillis = System.currentTimeMillis()
            graph.vertices
                .filter { it.lastDraggedMillis < previousFrameMillis }
                .forEach { it.pos = it.vertex.layout.pos }
        }
        while (lastSecondIterationsMillis.isNotEmpty() && System.currentTimeMillis() - lastSecondIterationsMillis.peek() > 1000L)
            lastSecondIterationsMillis.poll()
        uiProps.iterationsPerSecond = lastSecondIterationsMillis.size
        if (layoutAlgorithm.terminated) cancel()
        super.succeeded()
    }

    override fun cancel(): Boolean {
        uiProps.iterationsPerSecond = 0
        return super.cancel()
    }
}
