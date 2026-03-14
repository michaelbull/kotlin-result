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
    implementation("com.michael-bull.kotlin-result:kotlin-result:2.3.0")
}
```

A separate `kotlin-result-coroutines` artifact is available for coroutine support, shown in the
[Coroutines](#coroutines) section.

## Introduction

In functional programming, the [`Result`][result] type is a monadic type holding a returned
[value][result-value] or an [error][result-error].

To indicate an operation that succeeded, return an [`Ok(value)`][result-Ok] with the successful
`value`. If it failed, return an [`Err(error)`][result-Err] with the `error` that caused the
failure.

This defines a clear happy/unhappy path of execution commonly referred to as
[Railway Oriented Programming][rop], whereby the happy and unhappy paths are represented as
separate railways.

## Usage

The examples below use a customer service domain. A working application demonstrating these
patterns is available in the [`example`][example] directory.

### Creating Results

Return `Ok` or `Err` to indicate success or failure. A function that validates and parses an email
address might look like:

```kotlin
object EmailAddressParser {

    fun parse(address: String?): Result<EmailAddress, DomainMessage> {
        return when {
            address.isNullOrBlank() -> Err(EmailRequired)
            address.length > MAX_LENGTH -> Err(EmailTooLong)
            !address.matches(PATTERN) -> Err(EmailInvalid)
            else -> Ok(EmailAddress(address))
        }
    }
}
```

When interacting with code that may throw exceptions, wrap the call with
[`runCatching`][result-runCatching] to capture its execution as a `Result<T, Throwable>`:

```kotlin
val result: Result<Unit, Throwable> = runCatching {
    repository.save(customer)
}
```

Nullable types can be converted to a `Result` with [`toResultOr`][result-toResultOr]:

```kotlin
fun findById(id: CustomerId): Result<CustomerEntity, CustomerNotFound> {
    return repository.findById(id)
        .toResultOr { CustomerNotFound }
}
```

### Transforming Results

Use [`map`][result-map] to transform a success value:

```kotlin
fun getById(id: Long): Result<CustomerDto, DomainMessage> {
    return parseCustomerId(id)
        .andThen(::findById)
        .map(::entityToDto)
}
```

Use [`mapError`][result-mapError] to transform an error into a different type:

```kotlin
runCatching { repository.save(entity) }
    .mapError(::exceptionToDomainMessage)
```

Use [`mapBoth`][result-mapBoth] (also available as [`fold`][result-fold]) to handle both cases and
produce a single value. This is useful for mapping a `Result` to an HTTP response:

```kotlin
val (status, body) = customerService.getById(id)
    .mapBoth(
        { customer -> HttpStatusCode.OK to customer },
        { error -> HttpStatusCode.BadRequest to error.message }
    )
```

### Chaining

Use [`andThen`][result-andThen] to chain operations where each step may fail, passing the success
value from one step to the next:

```kotlin
val (status, body) = call.parameters
    .readId()
    .andThen(::parseCustomerId)
    .andThen(::findById)
    .map(::entityToDto)
    .mapBoth(::customerToResponse, ::messageToResponse)
```

This works well for linear pipelines where each step's output feeds directly into the next.

### Binding

When a chain is not linear, later steps may need values from earlier steps that aren't the
immediately preceding one. With `andThen`, this forces nesting to keep intermediate values in scope,
producing the [arrow anti-pattern][arrow-anti-pattern]:

```kotlin
fun save(id: Long, dto: CustomerDto): Result<Event?, DomainMessage> {
    return parseCustomerId(id).andThen { customerId ->
        findById(customerId).andThen { existing ->
            validate(dto).andThen { validated ->
                updateEntity(customerId, existing, validated)
            }
        }
    }
}
```

The [`binding`][result-binding] function solves this by providing an imperative-style block where
each `.bind()` call unwraps a `Result` into a named variable. All intermediate values stay in scope
naturally, and any failure short-circuits the entire block:

```kotlin
fun save(id: Long, dto: CustomerDto): Result<Event?, DomainMessage> = binding {
    val customerId = parseCustomerId(id).bind()
    val existing = findById(customerId).bind()
    val validated = validate(dto).bind()
    updateEntity(customerId, existing, validated)
}
```

### Combining Results

Use [`zip`][result-zip] to combine multiple independent results, returning early with the first
error:

```kotlin
fun validate(dto: CustomerDto): Result<Customer, DomainMessage> {
    return zip(
        { PersonalNameParser.parse(dto.firstName, dto.lastName) },
        { EmailAddressParser.parse(dto.email) },
        ::Customer
    )
}
```

Use [`zipOrAccumulate`][result-zipOrAccumulate] to combine results while collecting all errors
instead of stopping at the first:

```kotlin
fun validate(dto: CustomerDto): Result<Customer, List<DomainMessage>> {
    return zipOrAccumulate(
        { PersonalNameParser.parse(dto.firstName, dto.lastName) },
        { EmailAddressParser.parse(dto.email) },
        ::Customer
    )
}
```

Both `zip` and `zipOrAccumulate` support 2-5 arity.

### Working with Collections

Extension functions on `Iterable<Result<V, E>>` make it straightforward to work with collections
of results.

Use [`combine`][result-combine] to turn a `List<Result<V, E>>` into a `Result<List<V>, E>`,
returning early with the first error:

```kotlin
val results: List<Result<EmailAddress, DomainMessage>> =
    addresses.map(EmailAddressParser::parse)

val combined: Result<List<EmailAddress>, DomainMessage> = results.combine()
```

Use [`partition`][result-partition] to split results into a `Pair<List<V>, List<E>>`:

```kotlin
val (validAddresses, errors) = addresses
    .map(EmailAddressParser::parse)
    .partition()
```

Use [`filterOk`][result-filterOk] and [`filterErr`][result-filterErr] to extract values or errors:

```kotlin
val validAddresses: List<EmailAddress> = results.filterOk()
val errors: List<DomainMessage> = results.filterErr()
```

Additional collection functions include `allOk`, `anyOk`, `countOk`, `countErr`, `onEachOk`, and
`onEachErr`. See the full list in [`Iterable.kt`][result-iterable].

### Coroutines

The `kotlin-result-coroutines` module provides coroutine-aware extensions:

```groovy
dependencies {
    implementation("com.michael-bull.kotlin-result:kotlin-result:2.3.0")
    implementation("com.michael-bull.kotlin-result:kotlin-result-coroutines:2.3.0")
}
```

#### coroutineBinding

[`coroutineBinding`][result-coroutineBinding] is the concurrent equivalent of `binding`. It runs
inside a [`coroutineScope`][kotlin-coroutineScope], enabling concurrent decomposition of work. When
any call to `bind()` fails, the scope is cancelled, cancelling all other children:

```kotlin
suspend fun fetchCustomerProfile(id: CustomerId): Result<CustomerProfile, DomainMessage> {
    return coroutineBinding {
        val customer = async { findById(id) }
        val orders = async { findOrderHistory(id) }
        CustomerProfile(customer.await(), orders.await())
    }
}
```

#### runSuspendCatching

[`runSuspendCatching`][result-runSuspendCatching] is a coroutine-safe variant of `runCatching`.
The standard library's `runCatching` catches `CancellationException`, which breaks cooperative
coroutine cancellation. `runSuspendCatching` rethrows it:

```kotlin
suspend fun findCustomer(id: CustomerId): Result<CustomerEntity, Throwable> {
    return runSuspendCatching {
        repository.findById(id)
    }
}
```

#### Flow Extensions

Extension functions on `Flow<Result<V, E>>` mirror the collection extensions: `filterOk`,
`filterErr`, `onEachOk`, `onEachErr`, `combine`, and `partition`. See the full list in
[`Flow.kt`][result-flow].

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
1.3 of the language in Oct 2018 as an experimental feature. Initially, it could not be used as a return type as it was
"intended to be used by compiler generated code only - namely coroutines".

Less than one week after stating that they ["do not encourage use of kotlin.Result"][stdlib-result-half-baked], the
JetBrains team announced that
they ["will allow returning kotlin.Result from functions"][stdlib-result-return-type-lifted] in version 1.5, releasing
May 2021 — three years after its introduction in 1.3. At this time, the team were deliberating on whether to guide users
towards contextual receivers to replace the Result paradigm. In later years, the context receivers experiment was
superseded by context parameters, which are still in an experimental state.

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
- JetBrains keep inventing their own domain-specific versions: [one][MetadataDeclarationsComparator],
  [two][parametersMap], [three][ChannelResult], [four][LineStatusTrackerManager],
  [five][LazyLoadingAccountsDetailsProvider], [six][VcsCodeVisionProvider] - thus proving the need for such a type but
  lacking commitment to a standardised solution.
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
- Feature parity with Result types from other languages including Elm, Gleam, Haskell, & Rust
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
- [Gleam](https://github.com/michaelbull/kotlin-result/wiki/Gleam)
- [Haskell](https://github.com/michaelbull/kotlin-result/wiki/Haskell)
- [Rust](https://github.com/michaelbull/kotlin-result/wiki/Rust)
- [Scala](https://github.com/michaelbull/kotlin-result/wiki/Scala)

### 6. What other languages & libraries inspired this one?

Inspiration for this library has been drawn from other languages in which the Result monad is present, including:

- [Elm](http://package.elm-lang.org/packages/elm-lang/core/latest/Result)
- [Gleam](https://hexdocs.pm/gleam_stdlib/gleam/result.html)
- [Haskell](https://hackage.haskell.org/package/base/docs/Data-Either.html)
- [Rust](https://doc.rust-lang.org/std/result/)
- [Scala](https://www.scala-lang.org/api/scala/util/Either.html)

Improvements on existing solutions such the stdlib include:

- Reduced runtime overhead with zero object allocations on the happy path
- Feature parity with Result types from other languages including Elm, Gleam, Haskell, & Rust
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
[kotlin-native-target-support]: https://kotlinlang.org/docs/native-target-support.html
[result]: https://github.com/michaelbull/kotlin-result/blob/master/kotlin-result/src/commonMain/kotlin/com/github/michaelbull/result/Result.kt#L52
[result-value]: https://github.com/michaelbull/kotlin-result/blob/master/kotlin-result/src/commonMain/kotlin/com/github/michaelbull/result/Result.kt#L58
[result-error]: https://github.com/michaelbull/kotlin-result/blob/master/kotlin-result/src/commonMain/kotlin/com/github/michaelbull/result/Result.kt#L63
[result-Ok]: https://github.com/michaelbull/kotlin-result/blob/master/kotlin-result/src/commonMain/kotlin/com/github/michaelbull/result/Result.kt#L11
[result-Err]: https://github.com/michaelbull/kotlin-result/blob/master/kotlin-result/src/commonMain/kotlin/com/github/michaelbull/result/Result.kt#L19
[rop]: https://fsharpforfunandprofit.com/rop/
[example]: https://github.com/michaelbull/kotlin-result/tree/master/example
[result-runCatching]: https://github.com/michaelbull/kotlin-result/blob/master/kotlin-result/src/commonMain/kotlin/com/github/michaelbull/result/Factory.kt#L11
[result-toResultOr]: https://github.com/michaelbull/kotlin-result/blob/master/kotlin-result/src/commonMain/kotlin/com/github/michaelbull/result/Factory.kt#L46
[result-map]: https://github.com/michaelbull/kotlin-result/blob/master/kotlin-result/src/commonMain/kotlin/com/github/michaelbull/result/Map.kt#L15
[result-mapError]: https://github.com/michaelbull/kotlin-result/blob/master/kotlin-result/src/commonMain/kotlin/com/github/michaelbull/result/Map.kt#L229
[result-mapBoth]: https://github.com/michaelbull/kotlin-result/blob/master/kotlin-result/src/commonMain/kotlin/com/github/michaelbull/result/Map.kt#L107
[result-fold]: https://github.com/michaelbull/kotlin-result/blob/master/kotlin-result/src/commonMain/kotlin/com/github/michaelbull/result/Map.kt#L134
[result-andThen]: https://github.com/michaelbull/kotlin-result/blob/master/kotlin-result/src/commonMain/kotlin/com/github/michaelbull/result/And.kt#L26
[arrow-anti-pattern]: https://blog.codinghorror.com/flattening-arrow-code/
[result-binding]: https://github.com/michaelbull/kotlin-result/blob/master/kotlin-result/src/commonMain/kotlin/com/github/michaelbull/result/Binding.kt#L28
[result-zip]: https://github.com/michaelbull/kotlin-result/blob/master/kotlin-result/src/commonMain/kotlin/com/github/michaelbull/result/Zip.kt#L14
[result-zipOrAccumulate]: https://github.com/michaelbull/kotlin-result/blob/master/kotlin-result/src/commonMain/kotlin/com/github/michaelbull/result/Zip.kt#L132
[result-combine]: https://github.com/michaelbull/kotlin-result/blob/master/kotlin-result/src/commonMain/kotlin/com/github/michaelbull/result/Iterable.kt
[result-partition]: https://github.com/michaelbull/kotlin-result/blob/master/kotlin-result/src/commonMain/kotlin/com/github/michaelbull/result/Iterable.kt
[result-filterOk]: https://github.com/michaelbull/kotlin-result/blob/master/kotlin-result/src/commonMain/kotlin/com/github/michaelbull/result/Iterable.kt
[result-filterErr]: https://github.com/michaelbull/kotlin-result/blob/master/kotlin-result/src/commonMain/kotlin/com/github/michaelbull/result/Iterable.kt
[result-iterable]: https://github.com/michaelbull/kotlin-result/blob/master/kotlin-result/src/commonMain/kotlin/com/github/michaelbull/result/Iterable.kt
[result-coroutineBinding]: https://github.com/michaelbull/kotlin-result/blob/master/kotlin-result-coroutines/src/commonMain/kotlin/com/github/michaelbull/result/coroutines/CoroutineBinding.kt#L42
[kotlin-coroutineScope]: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/coroutine-scope.html
[result-runSuspendCatching]: https://github.com/michaelbull/kotlin-result/blob/master/kotlin-result-coroutines/src/commonMain/kotlin/com/github/michaelbull/result/coroutines/RunSuspendCatching.kt#L16
[result-flow]: https://github.com/michaelbull/kotlin-result/blob/master/kotlin-result-coroutines/src/commonMain/kotlin/com/github/michaelbull/result/coroutines/flow/Flow.kt
[kotlin-inline-classes]: https://kotlinlang.org/docs/inline-classes.html
[wiki-Overhead]: https://github.com/michaelbull/kotlin-result/wiki/Overhead
[stdlib-result-half-baked]: https://discuss.kotlinlang.org/t/state-of-kotlin-result-vs-kotlin-result/21103/4
[stdlib-result-return-type-lifted]: https://discuss.kotlinlang.org/t/state-of-kotlin-result-vs-kotlin-result/21103/5
[stdlib-result-keep]: https://github.com/Kotlin/KEEP/blob/master/proposals/stdlib/result.md#error-handling-style-and-exceptions
[MetadataDeclarationsComparator]: https://github.com/JetBrains/kotlin/blob/c811992b611b8a725b6b55dafa574a0b145b5da3/native/commonizer/src/org/jetbrains/kotlin/commonizer/metadata/utils/MetadataDeclarationsComparator.kt#L43
[parametersMap]: https://github.com/JetBrains/kotlin/blob/c811992b611b8a725b6b55dafa574a0b145b5da3/compiler/cli/cli-common/src/org/jetbrains/kotlin/utils/parametersMap.kt#L60
[ChannelResult]: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.channels/-channel-result/
[LineStatusTrackerManager]: https://github.com/JetBrains/intellij-community/blob/d73a081b09fcb0f53308352a57ad54c0721f0443/platform/vcs-impl/src/com/intellij/openapi/vcs/impl/LineStatusTrackerManager.kt#L1406
[LazyLoadingAccountsDetailsProvider]: https://github.com/JetBrains/intellij-community/blob/d73a081b09fcb0f53308352a57ad54c0721f0443/platform/collaboration-tools/src/com/intellij/collaboration/auth/ui/LazyLoadingAccountsDetailsProvider.kt#L92
[VcsCodeVisionProvider]: https://github.com/JetBrains/intellij-community/blob/d73a081b09fcb0f53308352a57ad54c0721f0443/platform/vcs-impl/lang/src/com/intellij/codeInsight/hints/VcsCodeVisionProvider.kt#L287
[stdlib-result-runCatching]: https://github.com/JetBrains/kotlin/blob/v2.2.20/libraries/stdlib/src/kotlin/util/Result.kt#L144
[CancellationException]: https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.coroutines.cancellation/-cancellation-exception/
[rich-hickey-maybe-not]: https://www.youtube.com/watch?v=YR5WdGrpoug&t=657s
[result-throwIf]: https://github.com/michaelbull/kotlin-result/blob/master/kotlin-result/src/commonMain/kotlin/com/github/michaelbull/result/Or.kt#L54
[wiki]: https://github.com/michaelbull/kotlin-result/wiki
[unit-tests]: https://github.com/michaelbull/kotlin-result/tree/master/kotlin-result/src/commonTest/kotlin/com/github/michaelbull/result
[github]: https://github.com/michaelbull/kotlin-result

[badge-android]: http://img.shields.io/badge/-android-6EDB8D.svg?style=flat
[badge-jvm]: http://img.shields.io/badge/-jvm-DB413D.svg?style=flat
[badge-js]: http://img.shields.io/badge/-js-F8DB5D.svg?style=flat
[badge-nodejs]: https://img.shields.io/badge/-nodejs-68a063.svg?style=flat
[badge-linux]: http://img.shields.io/badge/-linux-2D3F6C.svg?style=flat
[badge-windows]: http://img.shields.io/badge/-windows-4D76CD.svg?style=flat
[badge-wasm]: https://img.shields.io/badge/-wasm-624FE8.svg?style=flat
[badge-ios]: http://img.shields.io/badge/-ios-CDCDCD.svg?style=flat
[badge-mac]: http://img.shields.io/badge/-macos-111111.svg?style=flat
[badge-tvos]: http://img.shields.io/badge/-tvos-808080.svg?style=flat
[badge-watchos]: http://img.shields.io/badge/-watchos-C0C0C0.svg?style=flat
[badge-js-ir]: https://img.shields.io/badge/support-[IR]-AAC4E0.svg?style=flat
[badge-android-native]: http://img.shields.io/badge/support-[AndroidNative]-6EDB8D.svg?style=flat
[badge-apple-silicon]: http://img.shields.io/badge/support-[AppleSilicon]-43BBFF.svg?style=flat
[//]: # (@formatter:on)
