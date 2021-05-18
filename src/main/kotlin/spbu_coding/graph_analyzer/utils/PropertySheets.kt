package spbu_coding.graph_analyzer.utils

import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.input.KeyCode
import javafx.scene.layout.HBox
import javafx.scene.text.Text
import org.controlsfx.control.PropertySheet
import org.controlsfx.property.editor.PropertyEditor
import tornadofx.add
import tornadofx.paddingLeft
import tornadofx.stringBinding

fun EventTarget.propertySheet(items: ObservableList<PropertySheet.Item>) = PropertySheet(items).apply {
    isModeSwitcherVisible = false
    isSearchBoxVisible = false
    updateOnEnter()
    usePlainTextForNonEditableItems()
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

fun PropertySheet.usePlainTextForNonEditableItems() {
    val oldFactory = propertyEditorFactory
    setPropertyEditorFactory { item ->
        if (item.isEditable) oldFactory.call(item)
        else object : PropertyEditor<Any?> {
            private val editorNode = HBox(Text(value.toString()).apply {
                isDisable = true
                item.observableValue.ifPresent { observableValue ->
                    textProperty().bind(observableValue.stringBinding { it.toString() })
                }
            }).apply { paddingLeft = 5.0 }

            override fun getEditor(): Node = editorNode
            override fun getValue(): Any? = item.value
            override fun setValue(value: Any?) {
                item.value = value
            }
        }
    }
}
