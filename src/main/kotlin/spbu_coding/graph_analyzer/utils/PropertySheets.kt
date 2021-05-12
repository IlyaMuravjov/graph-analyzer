package spbu_coding.graph_analyzer.utils

import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import org.controlsfx.control.PropertySheet
import org.controlsfx.property.editor.PropertyEditor
import tornadofx.add
import tornadofx.stringBinding

fun EventTarget.propertySheet(items: ObservableList<PropertySheet.Item>) = PropertySheet(items).apply {
    isModeSwitcherVisible = false
    isSearchBoxVisible = false
    updateOnEnter()
    autoUpdateReadOnlyObservableItems()
    this@propertySheet.add(this)
}

fun PropertySheet.updateOnEnter() {
    val oldFactory = propertyEditorFactory
    setPropertyEditorFactory { item ->
        oldFactory.call(item).apply {
            editor.setOnKeyPressed { event ->
                if (event.code == KeyCode.ENTER) {
                    event.consume()
                    requestFocus()
                }
            }
        }
    }
}

fun PropertySheet.autoUpdateReadOnlyObservableItems() {
    val oldFactory = propertyEditorFactory
    setPropertyEditorFactory { item ->
        val observableValue = item.observableValue.orElse(null)
        if (item.isEditable || observableValue == null) oldFactory.call(item)
        else object : PropertyEditor<Any?> {
            private val editorNode = TextField().apply {
                isDisable = true
                textProperty().bind(observableValue.stringBinding { it.toString() })
            }

            override fun getEditor(): Node = editorNode
            override fun getValue(): Any? = observableValue.value
            override fun setValue(value: Any?) = Unit
        }
    }
}
