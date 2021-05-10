package spbu_coding.graph_analyzer.model.impl.serialize.sqlite

import javafx.stage.FileChooser.ExtensionFilter
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.BatchInsertStatement
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import spbu_coding.graph_analyzer.model.FileGraphSerializer
import spbu_coding.graph_analyzer.model.Graph
import spbu_coding.graph_analyzer.model.Vertex
import spbu_coding.graph_analyzer.model.impl.VertexCentralityImpl
import spbu_coding.graph_analyzer.model.impl.VertexCommunityImpl
import spbu_coding.graph_analyzer.model.impl.VertexImpl
import spbu_coding.graph_analyzer.model.impl.buildGraph
import java.io.File
import java.sql.Connection

abstract class AbstractSQLiteGraphSerializer(private val verticesTable: AbstractVertices) : FileGraphSerializer {
    override val extensionFilter = ExtensionFilter("SQLite", "*.sqlite3", "*.sqlite", "*.db")

    protected abstract fun createVertex(it: ResultRow): Vertex
    protected abstract fun BatchInsertStatement.insertVertex(it: Vertex)

    override fun serialize(output: File, graph: Graph<Vertex>) {
        connectToDatabase(output)
        transaction {
            SchemaUtils.create(verticesTable, Edges)
            verticesTable.batchInsert(graph.vertices) { insertVertex(it) }
            Edges.batchInsert(graph.edges) {
                this[Edges.from] = it.from.name
                this[Edges.to] = it.to.name
                this[Edges.weight] = it.weight
            }
        }
    }

    override fun deserialize(input: File): Graph<Vertex> {
        connectToDatabase(input)
        return transaction {
            buildGraph<String, Vertex> {
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
    override fun createVertex(it: ResultRow) = VertexImpl(it[NonAnalyzedVertices.name])

    override fun BatchInsertStatement.insertVertex(it: Vertex) {
        this[NonAnalyzedVertices.name] = it.name
    }
}

object SQLiteAnalyzedGraphSerializer : AbstractSQLiteGraphSerializer(AnalyzedVertices) {
    override fun createVertex(it: ResultRow) = VertexImpl(
        name = it[AnalyzedVertices.name],
        community = VertexCommunityImpl(it[AnalyzedVertices.community]),
        centrality = VertexCentralityImpl(it[AnalyzedVertices.centrality])
    )

    override fun BatchInsertStatement.insertVertex(it: Vertex) {
        this[AnalyzedVertices.name] = it.name
        this[AnalyzedVertices.community] = it.community.id
        this[AnalyzedVertices.centrality] = it.centrality.value
    }
}
