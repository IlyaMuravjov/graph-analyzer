package spbu_coding.graph_analyzer.model.impl.serialize.neo4j

class Neo4jConnectionData(val uri: String, val username: String, val password: String) {
    override fun toString() = "Neo4j [uri=$uri, username=$username]"
}