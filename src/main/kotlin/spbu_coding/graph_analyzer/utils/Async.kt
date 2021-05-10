package spbu_coding.graph_analyzer.utils

import javafx.concurrent.Task
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.*

fun <T> UIComponent.runAsyncWithDialog(title: String, daemon: Boolean, func: FXTask<*>.() -> T): Task<T> {
    var task: Task<T>? = null
    val dialog = dialog(
        title = title,
        modality = Modality.NONE,
        stageStyle = StageStyle.UTILITY
    ) {
        stage.isResizable = false
        stage.setOnCloseRequest { task?.cancel() }
        currentWindow?.let { window ->
            stage.x = window.x + window.width - stage.width - 30.0
            stage.y = window.y + window.height - stage.height - 30.0
        }
    }
    task = runAsync(func = func, daemon = daemon)
        .success {
            dialog?.close()
        }.fail {
            dialog?.close()
        }.cancel {
            dialog?.close()
        }
    return task
}
