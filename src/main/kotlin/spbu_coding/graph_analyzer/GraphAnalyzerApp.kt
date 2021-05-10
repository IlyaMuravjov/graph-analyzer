package spbu_coding.graph_analyzer

import javafx.stage.Stage
import spbu_coding.graph_analyzer.view.MainView
import tornadofx.App
import tornadofx.launch

class GraphAnalyzerApp : App(MainView::class) {
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
