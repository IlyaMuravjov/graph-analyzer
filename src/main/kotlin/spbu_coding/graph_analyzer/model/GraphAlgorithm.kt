package spbu_coding.graph_analyzer.model

import javafx.concurrent.Task
import spbu_coding.graph_analyzer.utils.PropsHolder

interface GraphAlgorithm : PropsHolder {
    val displayName: String
    fun reset()
    fun fixAfterInterruption(): IterationResult
    fun refreshInputProps()
    fun refreshGraph()
    fun runIteration(task: Task<IterationResult>): IterationResult
    fun updateOutputProps()
    override fun toString(): String

    enum class IterationResult {
        UNFINISHED,
        PAUSED,
        TERMINATED
    }
}

interface GraphAlgorithmCategory {
    val displayName: String
    fun createAlgorithms(graph: Graph<Vertex>): List<GraphAlgorithm>
}
