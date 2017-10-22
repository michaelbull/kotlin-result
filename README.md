# kotlin-result

[![Release](https://jitpack.io/v/com.github.michaelbull/kotlin-result.svg)](https://jitpack.io/#com.github.michaelbull/kotlin-result) [![Build Status](https://travis-ci.org/michaelbull/kotlin-result.svg?branch=master)](https://travis-ci.org/michaelbull/kotlin-result) [![License](https://img.shields.io/github/license/michaelbull/kotlin-result.svg)](https://github.com/michaelbull/kotlin-result/blob/master/LICENSE)

[`Result<V, E>`][result] is a monad for modelling success ([`Ok`][result-ok]) or
failure ([`Error`][result-error]) operations.

## Inspiration

Inspiration for this library has been drawn from other languages in which the
Result monad is present, including:

- [Elm](http://package.elm-lang.org/packages/elm-lang/core/latest/Result)
- [Haskell](https://hackage.haskell.org/package/base-4.10.0.0/docs/Data-Either.html)
- [Rust](https://doc.rust-lang.org/std/result/)

It also iterates on other Result libraries written in Kotlin, namely:

- [danneu/kotlin-result](https://github.com/danneu/kotlin-result)
- [kittinunf/Result](https://github.com/kittinunf/Result)

Improvements on the existing solutions include:

- Feature parity with Result types from other languages including Elm, Haskell,
     & Rust
- Lax constraints on `value`/`error` nullability
- Lax constraints on the `error` type's inheritance (does not inherit from
    `Exception`)
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

```groovy
repositories {
    maven { url "https://jitpack.io" }
}

dependencies {
    compile 'com.github.michaelbull:kotlin-result:1.0.0'
}
```

## Getting Started

The [unit tests][unit-tests] are a good source of examples for using the library
as they cover every available method.

Mappings from common Result libraries are available on the [wiki][wiki]:

- [Elm][wiki-elm]
- [Haskell][wiki-haskell]
- [Rust][wiki-rust]

## Contributing

Bug reports and pull requests are welcome on [GitHub][github].

## License

This project is available under the terms of the ISC license. See the
[`LICENSE`](LICENSE) file for the copyright information and licensing terms.

[result]: https://github.com/michaelbull/kotlin-result/blob/master/src/main/kotlin/com/github/michaelbull/result/Result.kt#L10
[result-ok]: https://github.com/michaelbull/kotlin-result/blob/master/src/main/kotlin/com/github/michaelbull/result/Result.kt#L15
[result-error]: https://github.com/michaelbull/kotlin-result/blob/master/src/main/kotlin/com/github/michaelbull/result/Result.kt#L31
[unit-tests]: https://github.com/michaelbull/kotlin-result/tree/master/src/test/kotlin/com/github/michaelbull/result
[wiki]: https://github.com/michaelbull/kotlin-result/wiki
[wiki-elm]: https://github.com/michaelbull/kotlin-result/wiki/Elm
[wiki-haskell]: https://github.com/michaelbull/kotlin-result/wiki/Haskell
[wiki-rust]: https://github.com/michaelbull/kotlin-result/wiki/Rust
[github]: https://github.com/michaelbull/kotlin-result
