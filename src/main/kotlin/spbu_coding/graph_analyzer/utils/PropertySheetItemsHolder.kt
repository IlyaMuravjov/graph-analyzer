package spbu_coding.graph_analyzer.utils

import org.controlsfx.control.PropertySheet
import org.controlsfx.property.BeanProperty
import java.beans.PropertyDescriptor

object EmptyPropertySheetItemsHolder : CopyablePropertySheetItemsHolder<EmptyPropertySheetItemsHolder> {
    override fun copyWritableProps() = EmptyPropertySheetItemsHolder
    override val propertySheetItems: List<PropertySheet.Item> get() = emptyList()
}

interface CopyablePropertySheetItemsHolder<out P : CopyablePropertySheetItemsHolder<P>> : PropertySheetItemsHolder {
    fun copyWritableProps(): P
}

interface PropertySheetItemsHolder {
    val propertySheetItems: List<PropertySheet.Item>
    fun beanProperty(name: String, displayName: String = name, readOnly: Boolean = false): PropertySheet.Item =
        BeanProperty(
            this,
            PropertyDescriptor(
                name,
                this::class.java,
                "get${name.capitalize()}",
                if (readOnly) null else "set${name.capitalize()}"
            ).apply {
                this.displayName = displayName
            }
        )
}
