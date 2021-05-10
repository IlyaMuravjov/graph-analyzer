package spbu_coding.graph_analyzer.model.impl.serialize.sqlite

import org.jetbrains.exposed.sql.Table
import spbu_coding.graph_analyzer.model.MAX_VERTEX_NAME_LENGTH

object Edges : Table() {
    val from = varchar("from", MAX_VERTEX_NAME_LENGTH)
    val to = varchar("to", MAX_VERTEX_NAME_LENGTH)
    val weight = double("weight")
}
