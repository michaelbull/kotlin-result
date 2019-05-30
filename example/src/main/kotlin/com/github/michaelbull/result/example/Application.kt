package com.github.michaelbull.result.example

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.example.model.domain.Customer
import com.github.michaelbull.result.example.model.domain.CustomerCreated
import com.github.michaelbull.result.example.model.domain.CustomerId
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
import com.github.michaelbull.result.example.model.domain.FirstNameRequired
import com.github.michaelbull.result.example.model.domain.FirstNameTooLong
import com.github.michaelbull.result.example.model.domain.LastNameRequired
import com.github.michaelbull.result.example.model.domain.LastNameTooLong
import com.github.michaelbull.result.example.model.domain.SqlCustomerInvalid
import com.github.michaelbull.result.example.model.dto.CustomerDto
import com.github.michaelbull.result.example.service.CustomerService
import com.github.michaelbull.result.mapBoth
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.toResultOr
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
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
            call.parameters.readId()
                .andThen(CustomerId.Companion::create)
                .andThen(CustomerService::getById)
                .mapError(::messageToResponse)
                .mapBoth(
                    success = { customer ->
                        call.respond(HttpStatusCode.OK, CustomerDto.from(customer))
                    },
                    failure = { (status, message) ->
                        call.respond(status, message)
                    }
                )
        }

        post("/customers/{id}") {
            call.parameters.readId()
                .andThen { id ->
                    val dto = call.receive<CustomerDto>()
                    dto.id = id
                    Ok(dto)
                }
                .andThen(Customer.Companion::from)
                .andThen(CustomerService::upsert)
                .mapError(::messageToResponse)
                .mapBoth(
                    success = { event ->
                        if (event == null) {
                            call.respond(HttpStatusCode.NotModified)
                        } else {
                            val (status, message) = messageToResponse(event)
                            call.respond(status, message)
                        }
                    },
                    failure = { (status, message) ->
                        call.respond(status, message)
                    }
                )
        }
    }

}

private fun Parameters.readId(): Result<Long, DomainMessage> {
    return this["id"]
        ?.toLongOrNull()
        .toResultOr { CustomerRequired }
}

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
        Pair(HttpStatusCode.BadRequest, "There is an error in your request")

// events
    CustomerCreated ->
        Pair(HttpStatusCode.Created, "Customer created")

    is EmailAddressChanged ->
        Pair(HttpStatusCode.OK, "Email address changed from ${message.old} to ${message.new}")

// exposed errors
    CustomerNotFound ->
        Pair(HttpStatusCode.NotFound, "Unknown customer")

// internal errors
    SqlCustomerInvalid,
    DatabaseTimeout,
    is DatabaseError ->
        Pair(HttpStatusCode.InternalServerError, "Internal server error occurred")

}
