package spbu_coding.graph_analyzer.model.impl.serialize.sqlite

import javafx.geometry.Point2D
import javafx.scene.paint.Color
import javafx.stage.FileChooser.ExtensionFilter
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import spbu_coding.graph_analyzer.model.FileGraphSerializer
import spbu_coding.graph_analyzer.model.Graph
import spbu_coding.graph_analyzer.model.SerializableVertex
import spbu_coding.graph_analyzer.model.impl.buildGraph
import spbu_coding.graph_analyzer.model.impl.serialize.SerializableVertexImpl
import java.io.File
import java.sql.Connection

object SQLiteGraphSerializer : FileGraphSerializer {
    override val extensionFilter get() = ExtensionFilter("SQLite", "*.sqlite3", "*.sqlite", "*.db")

    override fun serialize(output: File, graph: Graph<SerializableVertex>) {
        connectToDatabase(output)
        transaction {
            SchemaUtils.drop(Vertices, Edges)
            SchemaUtils.create(Vertices, Edges)
            Vertices.batchInsert(graph.vertices) {
                this[Vertices.id] = it.id
                this[Vertices.x] = it.pos.x
                this[Vertices.y] = it.pos.y
                this[Vertices.radius] = it.radius
                this[Vertices.red] = it.color.red
                this[Vertices.green] = it.color.green
                this[Vertices.blue] = it.color.blue
            }
            Edges.batchInsert(graph.edges) {
                this[Edges.from] = it.from.id
                this[Edges.to] = it.to.id
                this[Edges.weight] = it.weight
            }
        }
    }

    override fun deserialize(input: File): Graph<SerializableVertex> {
        connectToDatabase(input)
        return transaction {
            buildGraph<String, SerializableVertex> {
                Vertices.selectAll().forEach {
                    addVertex(
                        it[Vertices.id].value,
                        SerializableVertexImpl(
                            id = it[Vertices.id].value,
                            pos = Point2D(it[Vertices.x], it[Vertices.y]),
                            radius = it[Vertices.radius],
                            color = Color.color(it[Vertices.red], it[Vertices.green], it[Vertices.blue])
                        )
                    )
                }
                Edges.selectAll().forEach {
                    addEdge(it[Edges.from].value, it[Edges.to].value, it[Edges.weight])
                }
            }
        }
    }

    private fun connectToDatabase(file: File) = Database.connect("jdbc:sqlite:${file.path}", "org.sqlite.JDBC").also {
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    }
}
