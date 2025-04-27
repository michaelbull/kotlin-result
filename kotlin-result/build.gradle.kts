plugins {
    id("kotlin-conventions")
    id("publish-conventions")
}

kotlin {
    explicitApi()

    compilerOptions {
        optIn.add("com.github.michaelbull.result.annotation.UnsafeResultValueAccess")
        optIn.add("com.github.michaelbull.result.annotation.UnsafeResultErrorAccess")
    }
}
