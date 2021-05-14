package spbu_coding.graph_analyzer

import io.mockk.every
import io.mockk.mockkStatic
import javafx.stage.Stage
import org.controlsfx.control.PropertySheet
import org.junit.jupiter.api.Test
import org.testfx.framework.junit5.ApplicationTest
import org.testfx.util.WaitForAsyncUtils.waitFor
import tornadofx.FileChooserMode
import tornadofx.chooseFile
import java.io.File
import java.util.concurrent.TimeUnit

class OpenSQLiteAnalyzedGraphIT : ApplicationTest() {
    override fun start(stage: Stage) {
        GraphAnalyzerApp().start(stage)
    }

    override fun stop() {
        GraphAnalyzerApp().stop()
    }

    @Test
    fun `when SQLite analyzed graph is opened then then displayed graph properties should be updated`() {
        mockkStatic(::chooseFile)
        every {
            chooseFile("Open", any(), any(), FileChooserMode.Single, any(), any())
        } returns listOf(File(javaClass.getResource("/analyzed-graph-sample.sqlite3").file))
        clickOn("File")
        clickOn("Open")
        waitFor(10L, TimeUnit.SECONDS) {
            val items = lookup(".property-sheet").queryAll<PropertySheet>().flatMap { it.items }
            val vertices = items.find { it.name == "Vertices" }?.value
            val edges = items.find { it.name == "Edges" }?.value
            val edgesRendered = items.find { it.name == "Edges rendered" }?.value
            vertices == 14 && edges == 13 && edgesRendered == 13L
        }
    }
}
