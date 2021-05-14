package spbu_coding.graph_analyzer.model.impl.serialize

import spbu_coding.graph_analyzer.model.FileGraphSerializer
import spbu_coding.graph_analyzer.model.impl.serialize.sqlite.SQLiteAnalyzedGraphSerializer

fun fileGraphSerializers(): List<FileGraphSerializer> = listOf(SQLiteAnalyzedGraphSerializer)
