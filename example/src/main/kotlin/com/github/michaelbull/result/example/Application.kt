package com.github.michaelbull.result.example

import com.github.michaelbull.result.*
import com.github.michaelbull.result.example.model.domain.Customer
import com.github.michaelbull.result.example.model.domain.CustomerId
import com.github.michaelbull.result.example.model.domain.DomainMessage
import com.github.michaelbull.result.example.model.dto.CustomerDto
import com.github.michaelbull.result.example.service.CustomerService
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.util.ValuesMap

fun Application.main() {
    install(DefaultHeaders)
    install(Compression)
    install(CallLogging)
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }

    routing {
        get("/customers/{id}") {
            readId(call.parameters)
                .andThen(CustomerId.Companion::create)
                .andThen(CustomerService::getById)
                .mapError(::messageToResponse)
                .mapBoth(
                    { call.respond(HttpStatusCode.OK, CustomerDto.from(it)) },
                    { call.respond(it.first, it.second) }
                )
        }

        post("/customers/{id}") {
            readId(call.parameters)
                .andThen {
                    val dto = call.receive<CustomerDto>()
                    dto.id = it
                    Ok(dto)
                }
                .andThen(Customer.Companion::from)
                .andThen(CustomerService::upsert)
                .mapError(::messageToResponse)
                .mapBoth(
                    { event ->
                        if (event == null) {
                            call.respond(HttpStatusCode.NotModified)
                        } else {
                            val (status, message) = messageToResponse(event)
                            call.respond(status, message)
                        }
                    },
                    { call.respond(it.first, it.second) }
                )
        }
    }

}

private fun readId(values: ValuesMap): Result<Long, DomainMessage> {
    val id = values["id"]?.toLongOrNull()
    return if (id != null) Ok(id) else Err(DomainMessage.CustomerRequired)
}

private fun messageToResponse(message: DomainMessage) = when (message) {
    DomainMessage.CustomerRequired,
    DomainMessage.CustomerIdMustBePositive,
    DomainMessage.FirstNameRequired,
    DomainMessage.FirstNameTooLong,
    DomainMessage.LastNameRequired,
    DomainMessage.LastNameTooLong,
    DomainMessage.EmailRequired,
    DomainMessage.EmailTooLong,
    DomainMessage.EmailInvalid ->
        Pair(HttpStatusCode.BadRequest, "There is an error in your request")

// events
    DomainMessage.CustomerCreated ->
        Pair(HttpStatusCode.Created, "Customer created")

    is DomainMessage.EmailAddressChanged ->
        Pair(HttpStatusCode.OK, "Email address changed from ${message.old} to ${message.new}")

// exposed errors
    DomainMessage.CustomerNotFound ->
        Pair(HttpStatusCode.NotFound, "Unknown customer")

// internal errors
    DomainMessage.SqlCustomerInvalid,
    DomainMessage.DatabaseTimeout,
    is DomainMessage.DatabaseError ->
        Pair(HttpStatusCode.InternalServerError, "Internal server error occurred")

}
