package spbu_coding.graph_analyzer.utils

import javafx.beans.value.ObservableValue
import tornadofx.onChange
import tornadofx.toObservable

fun <T> ObservableValue<T>.onNull(op: () -> Unit) = onChange { if (it == null) op() }

fun <T, E> ObservableValue<T>.listBinding(op: (T?) -> List<E>) = op(value).toObservable().also { list ->
    onChange { list.setAll(op(it)) }
}
