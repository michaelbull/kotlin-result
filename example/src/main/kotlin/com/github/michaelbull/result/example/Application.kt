package com.github.michaelbull.result.example

import com.fasterxml.jackson.databind.SerializationFeature
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.example.model.domain.Created
import com.github.michaelbull.result.example.model.domain.CustomerIdMustBePositive
import com.github.michaelbull.result.example.model.domain.CustomerNotFound
import com.github.michaelbull.result.example.model.domain.CustomerRequired
import com.github.michaelbull.result.example.model.domain.DatabaseError
import com.github.michaelbull.result.example.model.domain.DatabaseTimeout
import com.github.michaelbull.result.example.model.domain.DomainMessage
import com.github.michaelbull.result.example.model.domain.EmailAddressChanged
import com.github.michaelbull.result.example.model.domain.EmailInvalid
import com.github.michaelbull.result.example.model.domain.EmailRequired
import com.github.michaelbull.result.example.model.domain.EmailTooLong
import com.github.michaelbull.result.example.model.domain.Event
import com.github.michaelbull.result.example.model.domain.FirstNameChanged
import com.github.michaelbull.result.example.model.domain.FirstNameRequired
import com.github.michaelbull.result.example.model.domain.FirstNameTooLong
import com.github.michaelbull.result.example.model.domain.LastNameChanged
import com.github.michaelbull.result.example.model.domain.LastNameRequired
import com.github.michaelbull.result.example.model.domain.LastNameTooLong
import com.github.michaelbull.result.example.model.domain.SqlCustomerInvalid
import com.github.michaelbull.result.example.model.dto.CustomerDto
import com.github.michaelbull.result.example.model.entity.CustomerEntity
import com.github.michaelbull.result.example.model.entity.CustomerId
import com.github.michaelbull.result.example.repository.InMemoryCustomerRepository
import com.github.michaelbull.result.example.service.CustomerService
import com.github.michaelbull.result.mapBoth
import com.github.michaelbull.result.toResultOr
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureRouting()
        configureSerialization()
    }.start(wait = true)
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
}

fun Application.configureRouting() {
    val customers = setOf(
        CustomerEntity(CustomerId(1L), "Michael", "Bull", "michael@example.com"),
        CustomerEntity(CustomerId(2L), "Kevin", "Herron", "kevin@example.com"),
        CustomerEntity(CustomerId(3L), "Markus", "Padourek", "markus@example.com"),
        CustomerEntity(CustomerId(4L), "Tristan", "Hamilton", "tristan@example.com"),
    )

    val customersById = customers.associateBy(CustomerEntity::id).toMutableMap()
    val customerRepository = InMemoryCustomerRepository(customersById)
    val customerService = CustomerService(customerRepository)

    routing {
        get("/customers/{id}") {
            val (status, message) = call.parameters.readId()
                .andThen(customerService::getById)
                .mapBoth(::customerToResponse, ::messageToResponse)

            call.respond(status, message)
        }

        post("/customers/{id}") {
            val (status, message) = call.parameters.readId()
                .andThen { customerService.save(it, call.receive()) }
                .mapBoth(::eventToResponse, ::messageToResponse)

            if (message != null) {
                call.respond(status, message)
            } else {
                call.respond(status)
            }
        }
    }
}

private fun Parameters.readId(): Result<Long, DomainMessage> {
    return get("id")
        ?.toLongOrNull()
        .toResultOr { CustomerRequired }
}

private fun customerToResponse(customer: CustomerDto) = HttpStatusCode.OK to customer

private fun messageToResponse(message: DomainMessage) = when (message) {
    CustomerRequired,
    CustomerIdMustBePositive,
    FirstNameRequired,
    FirstNameTooLong,
    LastNameRequired,
    LastNameTooLong,
    EmailRequired,
    EmailTooLong,
    EmailInvalid ->
        HttpStatusCode.BadRequest to "There is an error in your request"

// exposed errors
    CustomerNotFound ->
        HttpStatusCode.NotFound to "Unknown customer"

// internal errors
    SqlCustomerInvalid,
    DatabaseTimeout,
    is DatabaseError ->
        HttpStatusCode.InternalServerError to "Internal server error occurred"
}

private fun eventToResponse(event: Event?) = when (event) {
    null ->
        HttpStatusCode.NotModified to null

    Created ->
        HttpStatusCode.Created to "Customer created"

    is FirstNameChanged ->
        HttpStatusCode.OK to "First name changed from ${event.old} to ${event.new}"

    is LastNameChanged ->
        HttpStatusCode.OK to "Last name changed from ${event.old} to ${event.new}"

    is EmailAddressChanged ->
        HttpStatusCode.OK to "Email address changed from ${event.old} to ${event.new}"
}
