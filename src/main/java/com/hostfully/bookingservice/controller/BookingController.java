package com.hostfully.bookingservice.controller;

import com.hostfully.bookingservice.dto.BookingUpdateRequest;
import com.hostfully.bookingservice.model.Booking;
import com.hostfully.bookingservice.dto.BookingRequest;
import com.hostfully.bookingservice.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/bookings")
@Tag(name = "Bookings", description = "Booking management")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Operation(summary = "Create a booking")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Booking created"),
            @ApiResponse(responseCode = "400", description = "Invalid request or overlapping reservation"),
    })
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Booking> createBooking(@Valid @RequestBody BookingRequest request) {
        return bookingService.createBooking(request);
    }

    @Operation(summary = "Get a booking by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking found"),
            @ApiResponse(responseCode = "404", description = "Booking not found"),
    })
    @GetMapping("/{id}")
    public Mono<Booking> getBooking(@Parameter(description = "Booking ID") @PathVariable Long id) {
        return bookingService.getBooking(id);
    }

    @Operation(summary = "Update a booking")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request or overlapping reservation"),
            @ApiResponse(responseCode = "404", description = "Booking not found"),
    })
    @PutMapping("/{id}")
    public Mono<Booking> updateBooking(@Parameter(description = "Booking ID") @PathVariable Long id,
                                       @Valid @RequestBody BookingUpdateRequest request) {
        return bookingService.updateBooking(id, request);
    }

    @Operation(summary = "Delete a booking")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Booking deleted"),
            @ApiResponse(responseCode = "404", description = "Booking not found"),
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteBooking(@Parameter(description = "Booking ID") @PathVariable Long id) {
        return bookingService.deleteBooking(id);
    }

    @Operation(summary = "Cancel a booking")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking cancelled"),
            @ApiResponse(responseCode = "404", description = "Booking not found"),
    })
    @PostMapping("/{id}/cancel")
    public Mono<Booking> cancelBooking(@Parameter(description = "Booking ID") @PathVariable Long id) {
        return bookingService.cancelBooking(id);
    }

    @Operation(summary = "Rebook a cancelled booking")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking reinstated"),
            @ApiResponse(responseCode = "400", description = "Overlapping reservation"),
            @ApiResponse(responseCode = "404", description = "Booking not found"),
    })
    @PostMapping("/{id}/rebook")
    public Mono<Booking> rebookBooking(@Parameter(description = "Booking ID") @PathVariable Long id) {
        return bookingService.rebookBooking(id);
    }
}
