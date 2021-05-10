package spbu_coding.graph_analyzer

import javafx.stage.Stage
import tornadofx.App
import tornadofx.launch

class GraphAnalyzerApp : App() {
    override fun start(stage: Stage) {
        with(stage) {
            width = 1000.0
            height = 600.0
            isMaximized = true
        }
        super.start(stage)
    }
}

fun main(args: Array<String>) {
    launch<GraphAnalyzerApp>(args)
}
