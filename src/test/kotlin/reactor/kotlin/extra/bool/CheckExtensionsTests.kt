package reactor.kotlin.extra.bool

import org.junit.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class CheckExtensionsTests {

    @Test
    fun checkIfMonoTrue() {
        val value = 1
        StepVerifier.create(Mono.just(value).checkIf { true })
                .expectNext(value)
                .verifyComplete()
    }

    @Test
    fun checkIfMonoFalse() {
        StepVerifier.create(Mono.just(1).checkIf { false })
                .expectError(IllegalStateException::class.java)
                .verify()
    }

    @Test
    fun checkIfFluxAllTrue() {
        StepVerifier.create(Flux.fromArray(arrayOf(1, 2, 3)).checkIf { true })
                .expectNextCount(3)
                .expectComplete()
                .verify()
    }

    @Test
    fun checkIfFluxOneFalse() {
        StepVerifier.create(Flux.fromArray(arrayOf(1, 2, 3)).checkIf { it != 2 })
                .expectNextCount(1)
                .expectError(IllegalStateException::class.java)
                .verify()
    }

    @Test
    fun checkWhenMonoTrue() {
        val value = "dummy"
        StepVerifier.create(Mono.just(value).checkWhen { Mono.just(true) })
                .expectNext(value)
                .verifyComplete()
    }

    @Test
    fun checkWhenMonoFalse() {
        StepVerifier.create(Mono.just("dummy").checkWhen { Mono.just(false) })
                .expectError(IllegalStateException::class.java)
                .verify()
    }

    @Test
    fun checkWhenFluxAllTrue() {
        StepVerifier.create(Flux.fromArray(arrayOf(1, 2, 3)).checkWhen { Mono.just(true) })
                .expectNextCount(3)
                .expectComplete()
                .verify()
    }

    @Test
    fun checkWhenFluxOneFalse() {
        StepVerifier.create(Flux.fromArray(arrayOf(1, 2, 3)).checkWhen { Mono.just(it != 2) })
                .expectNextCount(1)
                .expectError(IllegalStateException::class.java)
                .verify()
    }
}