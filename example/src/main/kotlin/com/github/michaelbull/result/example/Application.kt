package com.github.michaelbull.result.example

import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.example.model.domain.Customer
import com.github.michaelbull.result.example.model.domain.CustomerId
import com.github.michaelbull.result.example.model.domain.DomainMessage
import com.github.michaelbull.result.example.model.dto.CustomerDto
import com.github.michaelbull.result.example.service.CustomerService
import com.github.michaelbull.result.mapBoth
import com.github.michaelbull.result.mapError
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
            val id = call.parameters["id"]?.toLongOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest)
            } else {
                CustomerId.create(id)
                    .andThen(CustomerService::getById)
                    .mapError(::messageToResponse)
                    .mapBoth(
                        success = { call.respond(HttpStatusCode.OK, CustomerDto.from(it)) },
                        failure = { call.respond(it.first, it.second) }
                    )
            }
        }

        post("/customers/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest)
            } else {
                val dto = call.receive<CustomerDto>()
                dto.id = id

                Customer.from(dto)
                    .andThen(CustomerService::upsert)
                    .mapError(::messageToResponse)
                    .mapBoth(
                        success = {
                            if (it == null) {
                                call.respond(HttpStatusCode.NotModified)
                            } else {
                                val (status, message) = messageToResponse(it)
                                call.respond(status, message)
                            }
                        },
                        failure = { call.respond(it.first, it.second) }
                    )
            }
        }
    }
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
