# kotlin-result

[![Maven Central](https://img.shields.io/maven-central/v/com.michael-bull.kotlin-result/kotlin-result.svg)](https://search.maven.org/search?q=g:com.michael-bull.kotlin-result)
[![CI](https://github.com/michaelbull/kotlin-result/actions/workflows/ci.yaml/badge.svg)](https://github.com/michaelbull/kotlin-result/actions/workflows/ci.yaml)
[![License](https://img.shields.io/github/license/michaelbull/kotlin-result.svg)](https://github.com/michaelbull/kotlin-result/blob/master/LICENSE)

![badge][badge-android]
![badge][badge-jvm]
![badge][badge-js]
![badge][badge-nodejs]
![badge][badge-linux]
![badge][badge-windows]
![badge][badge-wasm]
![badge][badge-ios]
![badge][badge-mac]
![badge][badge-tvos]
![badge][badge-watchos]
![badge][badge-js-ir]
![badge][badge-android-native]
![badge][badge-apple-silicon]

A multiplatform Result monad for modelling success or failure operations, providing all three tiers
of [Kotlin/Native target support][kotlin-native-target-support].

## Installation

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation("com.michael-bull.kotlin-result:kotlin-result:2.1.0")
}
```

## Introduction

In functional programming, the result [`Result`][result] type is a monadic type holding a returned [value][result-value]
or an [error][result-error].

To indicate an operation that succeeded, return an [`Ok(value)`][result-Ok] with the successful `value`. If it failed,
return an [`Err(error)`][result-Err] with the `error` that caused the failure.

This helps to define a clear happy/unhappy path of execution that is commonly referred to
as [Railway Oriented Programming][rop], whereby the happy and unhappy paths are represented as separate railways.

## Getting Started

Below is a simple example of how you may use the `Result` type to model a function that may fail.

```kotlin
fun checkPrivileges(user: User, command: Command): Result<Command, CommandError> {
    return if (user.rank >= command.minimumRank) {
        Ok(command)
    } else {
        Err(CommandError.InsufficientRank(command.name))
    }
}
```

When interacting with code outside of your control that may throw exceptions, wrap the call
with [`runCatching`][result-runCatching] to capture its execution as a `Result<T, Throwable>`:

```kotlin
val result: Result<Customer, Throwable> = runCatching {
    customerDb.findById(id = 50) // could throw SQLException or similar
}
```

Nullable types, such as the `find` method in the example below, can be converted to a `Result` using the `toResultOr`
extension function.

```kotlin
val result: Result<Customer, String> = customers
    .find { it.id == id } // returns Customer?
    .toResultOr { "No customer found" }
```

### Transforming Results

Both success and failure results can be transformed within a stage of the railway track. The example below demonstrates
how to transform an internal program error `UnlockError` into the exposed client error `IncorrectPassword`.

```kotlin
val result: Result<Treasure, UnlockResponse> =
    unlockVault("my-password") // returns Result<Treasure, UnlockError>
        .mapError { IncorrectPassword } // transform UnlockError into IncorrectPassword
```

### Chaining

Results can be chained to produce a "happy path" of execution. For example, the happy path for a user entering commands
into an administrative console would consist of: the command being tokenized, the command being registered, the user
having sufficient privileges, and the command executing the associated action. The example below uses the
`checkPrivileges` function we defined earlier.

```kotlin
tokenize(command.toLowerCase())
    .andThen(::findCommand)
    .andThen { cmd -> checkPrivileges(loggedInUser, cmd) }
    .andThen { execute(user = loggedInUser, command = cmd, timestamp = LocalDateTime.now()) }
    .mapBoth(
        { output -> printToConsole("returned: $output") },
        { error -> printToConsole("failed to execute, reason: ${error.reason}") }
    )
```

## Advanced Usage - Binding (Monad Comprehension)

The [`binding`][result-binding] function allows multiple calls that each return a `Result` to be chained imperatively.

When inside a `binding` block, the `bind()` function is accessible on any `Result`. Each call to `bind` will attempt to
unwrap the `Result` and store its value, returning early if any `Result` is an error.

In the example below, should `functionX()` return an error, then execution will skip both `functionY()` and
`functionZ()`, instead storing the error from `functionX` in the variable named `sum`.

```kotlin
fun functionX(): Result<Int, SumError> = TODO()
fun functionY(): Result<Int, SumError> = TODO()
fun functionZ(): Result<Int, SumError> = TODO()

val sum: Result<Int, SumError> = binding {
    val x = functionX().bind()
    val y = functionY().bind()
    val z = functionZ().bind()
    x + y + z
}

println("The sum is $sum") // prints "The sum is Ok(100)"
```

The `binding` function primarily draws inspiration from [Bow's `binding` function][bow-bindings], however below is a
list of other resources on the topic of monad comprehensions.

- [Monad comprehensions - Arrow (Kotlin)](https://old.arrow-kt.io/docs/patterns/monad_comprehensions/)
- [Monad comprehensions - Bow (Swift)](https://bow-swift.io/docs/patterns/monad-comprehensions)
- [For comprehensions - Scala](https://docs.scala-lang.org/tour/for-comprehensions.html)

#### Coroutine Binding Support

Use of suspending functions within a `coroutineBinding` block requires an additional dependency:

```kotlin
dependencies {
    implementation("com.michael-bull.kotlin-result:kotlin-result:2.1.0")
    implementation("com.michael-bull.kotlin-result:kotlin-result-coroutines:2.1.0")
}
```

The [`coroutineBinding`][result-coroutineBinding] function runs inside a [`coroutineScope`][kotlin-coroutineScope],
facilitating _concurrent decomposition of work_.

When any call to `bind()` inside the block fails, the scope fails, cancelling all other children.

The example below demonstrates a computationally expensive function that takes five milliseconds to compute being
eagerly cancelled as soon as a smaller function fails in just one millisecond:

```kotlin
suspend fun failsIn5ms(): Result<Int, DomainErrorA> = TODO()
suspend fun failsIn1ms(): Result<Int, DomainErrorB> = TODO()

runBlocking {
    val result: Result<Int, BindingError> = coroutineBinding { // this creates a new CoroutineScope
        val x = async { failsIn5ms().bind() }
        val y = async { failsIn1ms().bind() }
        x.await() + y.await()
    }

    // result will be Err(DomainErrorB)
}
```

## FAQs

### 1. What is the performance cost?

The `Result` type is modelled as an [inline value class][kotlin-inline-classes]. This achieves zero object allocations
on the happy path. A full breakdown, with example output Java code, is available in the [Overhead][wiki-Overhead] design
doc.

### 2. Why not use `kotlin.Result` from the standard library?

> "`kotlin.Result` is half-baked"
>
> — [Ilmir Usmanov, JetBrains][stdlib-result-half-baked]

This library was created in Oct 2017. The JetBrains team introduced `kotlin.Result` to the standard library in version
1.3 of the language in Oct 2018 as an experimental feature. Initially, it was limited to internal use only as it was
"intended to be used by compiler generated code only - namely coroutines".

Less than one week after stating that ["we do not encourage use of kotlin.Result"][stdlib-result-half-baked], the
JetBrains team announced that
they ["will allow returning kotlin.Result from functions"][stdlib-result-return-type-lifted]. This came at the time when
they were considering guiding users towards contextual receivers to replace the Result paradigm. In later years, the
context receivers experiment was superseded by the more recent context parameters, which are still in an experimental
state.

Michail Zarečenskij, the Lead Language Designer for Kotlin, announced at KotlinConf 2025 the development of
["Rich Errors in Kotlin"](https://2025.kotlinconf.com/talks/762779/), providing yet another potential solution for error
handling.

As of the time of writing, the KEEP for `kotlin.Result` states that it is ["not designed to represent domain-specific
error conditions"][stdlib-result-keep]. This statement should help to inform most users with their decision of adopting
it as a return type for generic business logic.

> "The Result class is not designed to represent domain-specific error conditions."
>
> — [Kotlin Evolution and Enhancement Process #127][stdlib-result-keep]

#### Reasons against `kotlin.Result`:

- The functionality it provides does not match that of a first class citizen Result type found in other languages, nor
  the functionality offered by this library.
- The Kotlin team admits its "half-baked" and discourages use for "domain-specific error conditions".
- The Kotlin team do not use it, and are sending increasingly mixed messages on how users should be dealing with
  domain-specific errors.
- The Kotlin team keep inventing their own domain-specific versions, e.g. [`ChannelResult`][ChannelResult]) in the
  coroutines library, thus proving the need for such a type but lacking commitment to a standardised solution.
- It was initially unusable as a return type and usage was discouraged. This restriction was then lifted and users
  guided towards context receivers. Context receivers were abandoned in favour of the (still experimental) context
  parameters. Rich errors have been proposed to supersede context parameters by providing a language-level solution.
- The [`runCatching`][stdlib-result-runCatching] implementation is **incompatible** with cooperatively cancelled
  coroutines. It catches all child types of `Throwable`, therefore catching a `CancellationException`. This is a special
  type of exception that ["indicates normal cancellation of a coroutine"][CancellationException]. Catching and not
  rethrowing it **will break** this behaviour. This library provides [`runSuspendCatching`][result-runSuspendCatching]
  to address this.
- Error types are constrained to being subclasses of `Throwable`. This means you must inherit from `Throwable` in all of
  your domain-specific errors. This comes with the trappings of stacktraces being computed per-instantiation, and errors
  now being throwable generally across your codebase regardless of whether you intend for consumers to throw them.
- Instantiation is verbose with factory functions being under the `Result` companion object: `Result.success`,
  `Result.failure`

#### Reasons for `kotlin-result` over `kotlin.Result`:

- Consistent naming with existing Result libraries from other languages (e.g. `map`, `mapError`, `mapBoth`, `mapEither`,
  `and`, `andThen`, `or`, `orElse`, `unwrap`)
- Feature parity with Result types from other languages including Elm, Haskell, & Rust
- Extension functions on `Iterable` & `List` for folding, combining, partitioning
- Monadic comprehension support via the `binding` and `coroutineBinding` functions for imperative use
- Coroutine-aware primitives e.g. `coroutineBinding` and `runSuspendCatching`
- Lax constraints on the `error` type's inheritance (does not inherit from `Throwable`)
- Top-level `Ok` and `Err` functions for instantiation brevity

### 3. Why not call it `Either`?

> "`Either` in particular, wow it is just not a beautiful thing. It does not mean OR. It's got a left and a right, it
> should have been called 'left right thingy'. Then you'd have a better sense of the true semantics; there are no
> semantics except what you superimpose on top of it."
>
> — [Rich Hickey, author of Closure][rich-hickey-maybe-not]

`Result` is opinionated in name and nature with a strict definition. It models its success as the left generic
parameter and failure on the right. This decision removes the need for users to choose a "biased" side which is a
repeated point of contention for anyone using the more broadly named `Either` type. As such there is no risk of
different libraries/teams/projects using different sides for bias.

`Either` itself is misleading and harmful. It is a naive attempt to add a true `OR` type to the type system. It has no
pre-defined semantics, and is missing the properties of a truly mathematical `OR`:

- **Not Commutative**: `Either<String, Int>` is not the same as the type `Either<Int, String>`. The order of the types
  is fixed, as the positions themselves have different conventional meanings.
- **Not Symmetric**: `Either<String, Int>` has left and right components are not treated as equals. They are designed
  for different roles: `String` for the success value and `Int` for the error value. They are not interchangeable.

### 4. Why does [`runCatching`][result-runCatching] catch `Throwable`?

For consistency with the standard libraries own [`runCatching`][stdlib-result-runCatching].

To address the issue of breaking coroutine cancellation behaviour, we introduced the
[`runSuspendCatching`][result-runSuspendCatching] variant which explicitly rethrows any
[`CancellationException`][CancellationException].

Should you need to rethrow a specific type of throwable, use [`throwIf`][result-throwIf]:

```kotlin
runCatching(block).throwIf { error ->
    error is IOException
}
```

### 5. I've used `Result` in another language, how does it translate?

Mappings are available on the [wiki][wiki] to assist those with experience using the `Result` type in other languages:

- [Elm](https://github.com/michaelbull/kotlin-result/wiki/Elm)
- [Haskell](https://github.com/michaelbull/kotlin-result/wiki/Haskell)
- [Rust](https://github.com/michaelbull/kotlin-result/wiki/Rust)
- [Scala](https://github.com/michaelbull/kotlin-result/wiki/Scala)

### 6. What other languages & libraries inspired this one?

Inspiration for this library has been drawn from other languages in which the Result monad is present, including:

- [Elm](http://package.elm-lang.org/packages/elm-lang/core/latest/Result)
- [Haskell](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html)
- [Rust](https://doc.rust-lang.org/std/result/)
- [Scala](http://www.scala-lang.org/api/2.12.4/scala/util/Either.html)

Improvements on existing solutions such the stdlib include:

- Reduced runtime overhead with zero object allocations on the happy path
- Feature parity with Result types from other languages including Elm, Haskell, & Rust
- Lax constraints on `value`/`error` nullability
- Lax constraints on the `error` type's inheritance (does not inherit from `Exception`)
- Top-level `Ok` and `Err` functions avoids qualifying usages with `Result.Ok`/`Result.Err` respectively
- Higher-order functions marked with the `inline` keyword for reduced runtime overhead
- Extension functions on `Iterable` & `List` for folding, combining, partitioning
- Consistent naming with existing Result libraries from other languages (e.g. `map`, `mapError`, `mapBoth`, `mapEither`,
  `and`, `andThen`, `or`, `orElse`, `unwrap`)
- Extensive test suite with almost 100 [unit tests][unit-tests] covering every library method

### 7. Where can I learn more?

Below is a collection of videos & articles authored on the subject of this library. Feel free to open a pull request
on [GitHub][github] if you would like to include yours.

- [[EN] The Result Monad - Adam Bennett](https://adambennett.dev/2020/05/the-result-monad/)
- [[EN] A Functional Approach to Exception Handling - Tristan Hamilton](https://youtu.be/bEC_t8dH23c?t=132)
- [[EN] kotlin: A functional gold mine - Mark Bucciarelli](http://markbucciarelli.com/posts/2020-01-04_kotlin_functional_gold_mine.html)
- [[EN] Railway Oriented Programming - Scott Wlaschin](https://fsharpforfunandprofit.com/rop/)
- [[JP] KotlinでResult型使うならkotlin-resultを使おう](https://note.com/telin/n/n6d9e352c344c)
- [[JP] kotlinのコードにReturn Resultを組み込む](https://nnao45.hatenadiary.com/entry/2019/11/30/224820)
- [[JP] kotlin-resultを半年使ってみて](https://zenn.dev/loglass/articles/try-using-kotlin-result)
- [[JP] kotlin-result入門](https://blog.nnn.dev/entry/2023/06/22/110000)

## Contributing

Bug reports and pull requests are welcome on [GitHub][github].

## License

This project is available under the terms of the ISC license. See the [`LICENSE`](LICENSE) file for the copyright
information and licensing terms.

[//]: # (@formatter:off)
[result]: https://github.com/michaelbull/kotlin-result/blob/master/kotlin-result/src/commonMain/kotlin/com/github/michaelbull/result/Result.kt#L10
[result-value]: https://github.com/michaelbull/kotlin-result/blob/master/kotlin-result/src/commonMain/kotlin/com/github/michaelbull/result/Result.kt#L55
[result-error]: https://github.com/michaelbull/kotlin-result/blob/master/kotlin-result/src/commonMain/kotlin/com/github/michaelbull/result/Result.kt#L59
[result-Ok]: https://github.com/michaelbull/kotlin-result/blob/master/kotlin-result/src/commonMain/kotlin/com/github/michaelbull/result/Result.kt#L9
[result-Err]: https://github.com/michaelbull/kotlin-result/blob/master/kotlin-result/src/commonMain/kotlin/com/github/michaelbull/result/Result.kt#L17
[kotlin-inline-classes]: https://kotlinlang.org/docs/inline-classes.html
[wiki-Overhead]: https://github.com/michaelbull/kotlin-result/wiki/Overhead
[rop]: https://fsharpforfunandprofit.com/rop/
[kotlin-native-target-support]: https://kotlinlang.org/docs/native-target-support.html
[github]: https://github.com/michaelbull/kotlin-result
[wiki]: https://github.com/michaelbull/kotlin-result/wiki
[result-runCatching]: https://github.com/michaelbull/kotlin-result/blob/master/kotlin-result/src/commonMain/kotlin/com/github/michaelbull/result/Factory.kt#L11
[result-runSuspendCatching]: https://github.com/michaelbull/kotlin-result/blob/master/kotlin-result-coroutines/src/commonMain/kotlin/com/github/michaelbull/result/coroutines/RunSuspendCatching.kt#L17
[result-throwIf]: https://github.com/michaelbull/kotlin-result/blob/master/kotlin-result/src/commonMain/kotlin/com/github/michaelbull/result/Or.kt#L52
[result-binding]: https://github.com/michaelbull/kotlin-result/blob/master/kotlin-result/src/commonMain/kotlin/com/github/michaelbull/result/Binding.kt#L28
[bow-bindings]: https://bow-swift.io/docs/patterns/monad-comprehensions/#bindings
[result-coroutineBinding]: https://github.com/michaelbull/kotlin-result/blob/master/kotlin-result-coroutines/src/commonMain/kotlin/com/github/michaelbull/result/coroutines/CoroutineBinding.kt#L42
[kotlin-coroutineScope]: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/coroutine-scope.html
[unit-tests]: https://github.com/michaelbull/kotlin-result/tree/master/kotlin-result/src/commonTest/kotlin/com/github/michaelbull/result
[stdlib-result-half-baked]: https://discuss.kotlinlang.org/t/state-of-kotlin-result-vs-kotlin-result/21103/4
[stdlib-result-return-type-lifted]: https://discuss.kotlinlang.org/t/state-of-kotlin-result-vs-kotlin-result/21103/5
[stdlib-result-keep]: https://github.com/Kotlin/KEEP/blob/master/proposals/stdlib/result.md#error-handling-style-and-exceptions
[stdlib-result-runCatching]: https://github.com/JetBrains/kotlin/blob/v2.2.20/libraries/stdlib/src/kotlin/util/Result.kt#L144
[ChannelResult]: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.channels/-channel-result/
[CancellationException]: https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.coroutines.cancellation/-cancellation-exception/
[rich-hickey-maybe-not]: https://www.youtube.com/watch?v=YR5WdGrpoug&t=657s

[badge-android]: http://img.shields.io/badge/-android-6EDB8D.svg?style=flat
[badge-android-native]: http://img.shields.io/badge/support-[AndroidNative]-6EDB8D.svg?style=flat
[badge-jvm]: http://img.shields.io/badge/-jvm-DB413D.svg?style=flat
[badge-js]: http://img.shields.io/badge/-js-F8DB5D.svg?style=flat
[badge-js-ir]: https://img.shields.io/badge/support-[IR]-AAC4E0.svg?style=flat
[badge-nodejs]: https://img.shields.io/badge/-nodejs-68a063.svg?style=flat
[badge-linux]: http://img.shields.io/badge/-linux-2D3F6C.svg?style=flat
[badge-windows]: http://img.shields.io/badge/-windows-4D76CD.svg?style=flat
[badge-wasm]: https://img.shields.io/badge/-wasm-624FE8.svg?style=flat
[badge-apple-silicon]: http://img.shields.io/badge/support-[AppleSilicon]-43BBFF.svg?style=flat
[badge-ios]: http://img.shields.io/badge/-ios-CDCDCD.svg?style=flat
[badge-mac]: http://img.shields.io/badge/-macos-111111.svg?style=flat
[badge-watchos]: http://img.shields.io/badge/-watchos-C0C0C0.svg?style=flat
[badge-tvos]: http://img.shields.io/badge/-tvos-808080.svg?style=flat
[//]: # (@formatter:on)
