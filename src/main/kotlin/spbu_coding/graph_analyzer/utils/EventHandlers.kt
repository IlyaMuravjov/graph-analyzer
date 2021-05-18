package spbu_coding.graph_analyzer.utils

import javafx.concurrent.Task
import javafx.event.Event
import javafx.event.EventHandler

operator fun <T : Event> EventHandler<T>?.plus(other: EventHandler<T>) = EventHandler<T> {
    this?.handle(it)
    other.handle(it)
}

infix fun <T> Task<T>.addOnSuccess(func: (T) -> Unit) = apply { onSucceeded += { func(value) } }
infix fun <T> Task<T>.addOnFail(func: (Throwable) -> Unit) = apply { onFailed += { func(exception) } }
