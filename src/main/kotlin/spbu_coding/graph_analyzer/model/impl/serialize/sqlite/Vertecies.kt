package spbu_coding.graph_analyzer.model.impl.serialize.sqlite

import org.jetbrains.exposed.sql.Table
import spbu_coding.graph_analyzer.model.MAX_VERTEX_NAME_LENGTH

abstract class AbstractVertices : Table() {
    val name = varchar("name", MAX_VERTEX_NAME_LENGTH)
}

object NonAnalyzedVertices : AbstractVertices()

object AnalyzedVertices : AbstractVertices() {
    val community = integer("community").nullable()
    val centrality = double("centrality").nullable()
}
