package reactor.kotlin.extra.bool

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.SynchronousSink
import java.lang.Exception

/**
 * Extension to support checkWhen on [Mono], which works as filter.
 * However, unlike filter, it throws Exception if the condition check fails.
 *
 * @throws Exception if the condition check fails
 */
@Throws(Exception::class)
fun <K> Mono<K>.checkIf(onFailThrow: Exception = IllegalStateException("condition check failed"),
                        predicate: (K) -> Boolean)
        : Mono<K> = this.handle { source, sink -> check(onFailThrow, source, predicate(source), sink) }

/**
 * Extension to support checkWhen on [Flux], which works as filter.
 * However, unlike filter, it throws Exception if the condition check fails.
 *
 * @throws Exception if the condition check fails
 */
@Throws(Exception::class)
fun <K> Flux<K>.checkIf(onFailThrow: Exception = IllegalStateException("condition check failed"),
                        predicate: (K) -> Boolean)
        : Flux<K> = this.handle { source, sink -> check(onFailThrow, source, predicate(source), sink) }

/**
 * Extension to support checkWhen on [Mono], which works as filterWhen.
 * However, unlike filterWhen, it throws Exception if the condition check fails.
 *
 * @throws Exception if the condition check fails
 */
@Throws(Exception::class)
fun <K> Mono<K>.checkWhen(onFailThrow: Exception = IllegalStateException("condition check failed"),
                          predicate: (K) -> Mono<Boolean>)
        : Mono<K> = this.flatMap { source -> predicate(source).handle(checkWhen(onFailThrow, source)) }

/**
 * Extension to support checkWhen on [Flux], which works as filterWhen.
 * However, unlike filterWhen, it throws Exception if the condition check fails.
 *
 * @throws Exception if the condition check fails
 */
@Throws(Exception::class)
fun <K> Flux<K>.checkWhen(onFailThrow: Exception = IllegalStateException("condition check failed"),
                          predicate: (K) -> Mono<Boolean>)
        : Flux<K> = this.flatMap { source -> predicate(source).handle(checkWhen(onFailThrow, source)) }

private fun <K> checkWhen(onFailThrow: Exception, source: K): (Boolean, SynchronousSink<K>) -> Unit {
    return { checked, sink: SynchronousSink<K> -> check(onFailThrow, source, checked, sink) }
}

private fun <K> check(onFailThrow: Exception, source: K, checked: Boolean, sink: SynchronousSink<K>) {
    if (checked) sink.next(source) else sink.error(onFailThrow)
}