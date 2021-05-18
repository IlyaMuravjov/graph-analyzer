package spbu_coding.graph_analyzer.utils

import javafx.beans.Observable
import javafx.beans.binding.Binding
import javafx.beans.binding.Bindings
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import tornadofx.objectProperty
import tornadofx.onChange
import tornadofx.toObservable

fun <T> ObservableValue<T>.onNull(op: () -> Unit) = onChange { if (it == null) op() }

fun <T, E> ObservableValue<T>.toObservableList(op: (T) -> List<E>): ObservableList<E> =
    op(value).toObservable().also { list ->
        onChange { list.setAll(op(value)) }
    }

fun <T> ObservableValue<T>.toReadOnlyProperty(): ReadOnlyProperty<T> = objectProperty<T>().apply {
    bind(this@toReadOnlyProperty)
}

inline fun <T, R> ObservableValue<T>.nonNullBinding(
    vararg dependencies: Observable,
    crossinline op: (T) -> R
): Binding<R> = Bindings.createObjectBinding({ op(value) }, this, *dependencies)
