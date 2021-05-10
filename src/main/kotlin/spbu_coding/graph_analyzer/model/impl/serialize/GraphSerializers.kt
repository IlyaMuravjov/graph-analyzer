package spbu_coding.graph_analyzer.model.impl.serialize

import spbu_coding.graph_analyzer.model.FileGraphSerializer
import spbu_coding.graph_analyzer.model.GraphAnalysisType
import spbu_coding.graph_analyzer.model.impl.serialize.sqlite.SQLiteAnalyzedGraphSerializer
import spbu_coding.graph_analyzer.model.impl.serialize.sqlite.SQLiteNonAnalyzedGraphSerializer

fun fileGraphSerializers(analysisType: GraphAnalysisType): List<FileGraphSerializer> = when (analysisType) {
    GraphAnalysisType.ANALYZED -> listOf(SQLiteAnalyzedGraphSerializer)
    GraphAnalysisType.NON_ANALYZED -> listOf(SQLiteNonAnalyzedGraphSerializer)
}
