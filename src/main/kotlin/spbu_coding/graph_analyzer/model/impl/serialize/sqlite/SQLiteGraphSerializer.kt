package spbu_coding.graph_analyzer.model.impl.serialize.sqlite

import javafx.geometry.Point2D
import javafx.scene.paint.Color
import javafx.stage.FileChooser.ExtensionFilter
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.BatchInsertStatement
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import spbu_coding.graph_analyzer.model.FileGraphSerializer
import spbu_coding.graph_analyzer.model.Graph
import spbu_coding.graph_analyzer.model.SerializableVertex
import spbu_coding.graph_analyzer.model.impl.buildGraph
import java.io.File
import java.sql.Connection

abstract class AbstractSQLiteGraphSerializer(private val verticesTable: AbstractVertices) : FileGraphSerializer {
    override val extensionFilter = ExtensionFilter("SQLite", "*.sqlite3", "*.sqlite", "*.db")

    protected abstract fun createVertex(it: ResultRow): SerializableVertex
    protected abstract fun BatchInsertStatement.insertVertex(it: SerializableVertex)

    override fun serialize(output: File, graph: Graph<SerializableVertex>) {
        connectToDatabase(output)
        transaction {
            SchemaUtils.drop(verticesTable, Edges)
            SchemaUtils.create(verticesTable, Edges)
            verticesTable.batchInsert(graph.vertices) { insertVertex(it) }
            Edges.batchInsert(graph.edges) {
                this[Edges.from] = it.from.name
                this[Edges.to] = it.to.name
                this[Edges.weight] = it.weight
            }
        }
    }

    override fun deserialize(input: File): Graph<SerializableVertex> {
        connectToDatabase(input)
        return transaction {
            buildGraph<String, SerializableVertex> {
                verticesTable.selectAll().forEach {
                    addVertex(it[verticesTable.name], createVertex(it))
                }
                Edges.selectAll().forEach {
                    addEdge(it[Edges.from], it[Edges.to], it[Edges.weight])
                }
            }
        }
    }

    private fun connectToDatabase(file: File) = Database.connect("jdbc:sqlite:${file.path}", "org.sqlite.JDBC").also {
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    }
}

object SQLiteNonAnalyzedGraphSerializer : AbstractSQLiteGraphSerializer(NonAnalyzedVertices) {
    override fun createVertex(it: ResultRow) = SerializableVertex(it[NonAnalyzedVertices.name])

    override fun BatchInsertStatement.insertVertex(it: SerializableVertex) {
        this[NonAnalyzedVertices.name] = it.name
    }
}

object SQLiteAnalyzedGraphSerializer : AbstractSQLiteGraphSerializer(AnalyzedVertices) {
    override fun createVertex(it: ResultRow) = SerializableVertex(
        name = it[AnalyzedVertices.name],
        pos = Point2D(it[AnalyzedVertices.x], it[AnalyzedVertices.y]),
        radius = it[AnalyzedVertices.radius],
        color = Color(it[AnalyzedVertices.red], it[AnalyzedVertices.green], it[AnalyzedVertices.blue], 1.0)
    )

    override fun BatchInsertStatement.insertVertex(it: SerializableVertex) {
        this[AnalyzedVertices.name] = it.name
        this[AnalyzedVertices.x] = it.pos.x
        this[AnalyzedVertices.y] = it.pos.y
        this[AnalyzedVertices.radius] = it.radius
        this[AnalyzedVertices.red] = it.color.red
        this[AnalyzedVertices.green] = it.color.green
        this[AnalyzedVertices.blue] = it.color.blue
    }
}
