package spbu_coding.graph_analyzer.model

enum class GraphAnalysisType(val displayName: String) {
    ANALYZED("analyzed"),
    NON_ANALYZED("non-analyzed");

    override fun toString() = displayName
}
