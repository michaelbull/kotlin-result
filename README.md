# kotlin-result

[`Result<V, E>`][Result] is a monad for modelling success ([`Ok`][Result.Ok]) or
failure ([`Error`][Result.Error]) operations.

## Inspiration

Inspiration for this library has been drawn from other languages in which the
Result monad is present, including:

- [Elm](http://package.elm-lang.org/packages/elm-lang/core/latest/Result)
- [Haskell](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html)
- [Rust](https://doc.rust-lang.org/std/result/)

It also iterates on other Result libraries written in kotlin, namely:

- [`kotlin-result`](https://github.com/danneu/kotlin-result)
- [Result](https://github.com/kittinunf/Result)

Improvements on the existing solutions include:

- Relaxed constraints on nullable types (both the `value` and `error` can be
    typed as nullable)
- Relaxed constraints on the `error` type to not inherit from `Exception`
- Top level `Ok` and `Error` classes avoids qualifying usages with
    `Result.Ok`/`Result.Error` respectively
- Higher-order functions marked with the `inline` keyword for reduced runtime
    overhead
- Extension functions on `Iterable` & `List` for folding, combining, partitioning
- Consistent naming with existing Result libraries from other languages (e.g.
    `map`, `mapError`, `mapBoth`, `mapEither`, `and`, `andThen`, `or`, `orElse`,
    `unwrap`)
- Extensive test suite with over 50 unit tests and every library method covered


## Installation

This project is available in the [Maven Central Repository][maven-central]
repository. The artifacts are signed with my personal [GPG key][gpg].

```groovy
repositories {
    mavenCentral()
}

dependencies {
    compile 'com.mikebull94.kotlin-result:kotlin-result:1.0.0'
}
```

[Result]: https://github.com/michaelbull/kotlin-result/blob/master/src/main/kotlin/com/mikebull94/result/Result.kt#L8
[Result.Ok]: https://github.com/michaelbull/kotlin-result/blob/master/src/main/kotlin/com/mikebull94/result/Result.kt#L13
[Result.Error]: https://github.com/michaelbull/kotlin-result/blob/master/src/main/kotlin/com/mikebull94/result/Result.kt#L29a
[maven-central]: http://search.maven.org/
[gpg]:https://www.michael-bull.com/gpg.asc

