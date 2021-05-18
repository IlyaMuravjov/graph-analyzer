package spbu_coding.graph_analyzer.view

import javafx.geometry.Pos
import javafx.scene.input.KeyCode
import javafx.stage.Modality
import javafx.stage.Window
import spbu_coding.graph_analyzer.model.impl.serialize.neo4j.Neo4jCredentials
import spbu_coding.graph_analyzer.utils.propertySheet
import tornadofx.*

fun requestNeo4jCredentials(title: String, window: Window?, op: (Neo4jCredentials) -> Unit) =
    Neo4jCredentialsFragment(title, op).open(window)

class Neo4jCredentialsFragment(
    title: String,
    private val credentialsCallback: (Neo4jCredentials) -> Unit
) : Fragment(title) {
    private val credentials = Neo4jCredentials()

    override val root = vbox {
        propertySheet(credentials.propertySheetItems.toObservable())
        hbox(ViewConstants.SPACING) {
            padding = ViewConstants.INSETS
            alignment = Pos.CENTER
            button("  OK  ").action { onEnter() }
            button("Cancel").action { close() }
        }
        setOnKeyPressed { if (it.code == KeyCode.ENTER) onEnter() }
    }

    private fun onEnter() {
        close()
        credentialsCallback(credentials)
    }

    fun open(owner: Window?) {
        openWindow(
            modality = Modality.APPLICATION_MODAL,
            escapeClosesWindow = true,
            owner = owner,
            resizable = false
        )
    }
}
