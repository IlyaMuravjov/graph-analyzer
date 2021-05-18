package spbu_coding.graph_analyzer.model.impl.serialize.sqlite

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Table
import spbu_coding.graph_analyzer.model.MAX_VERTEX_NAME_LENGTH

object Vertices : IdTable<String>() {
    override val id = varchar("name", MAX_VERTEX_NAME_LENGTH).entityId()
    val x = double("x")
    val y = double("y")
    val radius = double("radius")
    val red = double("red")
    val green = double("green")
    val blue = double("blue")
}

object Edges : Table() {
    val from = reference("from", Vertices)
    val to = reference("to", Vertices)
    val weight = double("weight")
}
