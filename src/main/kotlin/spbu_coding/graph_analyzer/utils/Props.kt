package spbu_coding.graph_analyzer.utils

import org.controlsfx.control.PropertySheet

fun <T, P : Props<P>> sharingPropsFactory(props: P, create: SharingPropsFactory<T, P>.() -> T) =
    object : SharingPropsFactory<T, P> {
        override val props: P get() = props
        override fun create(): T = create(this)
    }

interface SharingPropsFactory<out T, P : Props<P>> : PropsHolder<P> {
    fun create(): T
}

open class RefreshablePropsHolderImpl<P : Props<P>>(val syncProps: P) : RefreshablePropsHolder<P> {
    override var props: P = syncProps.copyWritableProps()

    override fun refreshProps() {
        props = syncProps.copyWritableProps()
    }
}

interface RefreshablePropsHolder<P : Props<P>> : PropsHolder<P> {
    fun refreshProps()
}

interface PropsHolder<P : Props<P>> {
    val props: P
    val propertySheetItems get() = props.propertySheetItems
}

interface Props<P : Props<P>> {
    val propertySheetItems: List<PropertySheet.Item>
    fun copyWritableProps(): P
}
