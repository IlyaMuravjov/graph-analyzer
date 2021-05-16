package spbu_coding.graph_analyzer.utils

import javafx.beans.value.ObservableValue
import javafx.scene.control.PasswordField
import org.controlsfx.control.PropertySheet
import org.controlsfx.property.editor.AbstractPropertyEditor
import org.controlsfx.property.editor.PropertyEditor
import java.util.*

fun PropertySheet.Item.passwordItem() = object : PropertySheet.Item by this {
    override fun getPropertyEditorClass(): Optional<Class<out PropertyEditor<*>>> =
        Optional.of(PasswordEditor::class.java)
}

class PasswordEditor(
    item: PropertySheet.Item
) : AbstractPropertyEditor<String, PasswordField>(item, PasswordField()) {
    override fun setValue(value: String?) {
        editor.text = value.toString()
    }

    override fun getObservableValue(): ObservableValue<String> = editor.textProperty()
}
