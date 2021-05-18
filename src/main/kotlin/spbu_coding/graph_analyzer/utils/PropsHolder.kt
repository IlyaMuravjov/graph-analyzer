package spbu_coding.graph_analyzer.utils

import org.controlsfx.control.PropertySheet
import org.controlsfx.property.BeanProperty
import java.beans.PropertyDescriptor

interface PropsHolder {
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

interface CopyablePropsHolder<out P : CopyablePropsHolder<P>> : PropsHolder {
    fun copyInputProps(): P
}

object EmptyProps : CopyablePropsHolder<EmptyProps> {
    override fun copyInputProps() = EmptyProps
    override val propertySheetItems: List<PropertySheet.Item> get() = emptyList()
}
