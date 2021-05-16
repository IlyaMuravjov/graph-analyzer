package spbu_coding.graph_analyzer.model.impl.serialize.neo4j

import spbu_coding.graph_analyzer.utils.PropsHolder
import spbu_coding.graph_analyzer.utils.passwordItem

class Neo4jCredentials(var uri: String = "", var username: String = "", var password: String = "") : PropsHolder {
    override fun toString() = "Neo4j [uri=$uri, username=$username]"

    override val propertySheetItems = listOf(
        beanProperty("uri", "URI"),
        beanProperty("username", "Username"),
        beanProperty("password", "Password").passwordItem()
    )
}
