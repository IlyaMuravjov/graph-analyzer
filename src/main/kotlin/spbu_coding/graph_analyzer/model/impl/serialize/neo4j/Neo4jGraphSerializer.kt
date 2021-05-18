package spbu_coding.graph_analyzer.model.impl.serialize.neo4j

import javafx.geometry.Point2D
import javafx.scene.paint.Color
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.GraphDatabase
import spbu_coding.graph_analyzer.model.Graph
import spbu_coding.graph_analyzer.model.GraphSerializer
import spbu_coding.graph_analyzer.model.SerializableVertex
import spbu_coding.graph_analyzer.model.impl.buildGraph
import spbu_coding.graph_analyzer.model.impl.serialize.SerializableVertexImpl

object Neo4jGraphSerializer : GraphSerializer<Neo4jCredentials> {
    override fun serialize(output: Neo4jCredentials, graph: Graph<SerializableVertex>): Unit =
        GraphDatabase.driver(output.uri, AuthTokens.basic(output.username, output.password)).use { driver ->
            driver.session().use { session ->
                session.writeTransaction { transaction ->
                    transaction.run("MATCH (v) DETACH DELETE v")
                    for (vertex in graph.vertices) transaction.run(
                        "CREATE (v:Vertex {id: \$id, x: \$x, y: \$y, radius: \$radius, red: \$red, green: \$green, blue: \$blue})",
                        mutableMapOf(
                            "id" to vertex.id,
                            "x" to vertex.pos.x,
                            "y" to vertex.pos.y,
                            "radius" to vertex.radius,
                            "red" to vertex.color.red,
                            "green" to vertex.color.green,
                            "blue" to vertex.color.blue
                        ) as Map<String, Any>?
                    )
                    for (edge in graph.edges) transaction.run(
                        "MATCH (v1:Vertex {id: \$from}) MATCH (v2:Vertex {id: \$to}) MERGE (v1)-[:EDGE {weight: \$weight}]->(v2)",
                        mutableMapOf(
                            "from" to edge.from.id,
                            "to" to edge.to.id,
                            "weight" to edge.weight
                        ) as Map<String, Any>?
                    )
                }
            }
        }

    override fun deserialize(input: Neo4jCredentials): Graph<SerializableVertex> =
        GraphDatabase.driver(input.uri, AuthTokens.basic(input.username, input.password)).use { driver ->
            driver.session().use { session ->
                session.readTransaction { transaction ->
                    buildGraph<String, SerializableVertex> {
                        transaction.run("MATCH (v:Vertex) RETURN v.id AS id, v.x AS x, v.y AS y, v.radius AS radius, v.red AS red, v.green AS green, v.blue AS blue")
                            .forEach { vertex ->
                                addVertex(
                                    vertex["id"].asString(),
                                    SerializableVertexImpl(
                                        id = vertex["id"].asString(),
                                        pos = Point2D(vertex["x"].asDouble(), vertex["y"].asDouble()),
                                        radius = vertex["radius"].asDouble(),
                                        color = Color.color(
                                            vertex["red"].asDouble(),
                                            vertex["green"].asDouble(),
                                            vertex["blue"].asDouble()
                                        )
                                    )
                                )
                            }
                        transaction.run("MATCH (v1: Vertex)-[e:EDGE]->(v2: Vertex) RETURN e.weight AS weight, v1.id as from, v2.id as to")
                            .forEach { edge ->
                                addEdge(edge["from"].asString(), edge["to"].asString(), edge["weight"].asDouble())
                            }
                    }
                }
            }
        }
}
