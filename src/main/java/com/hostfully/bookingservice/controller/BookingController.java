package com.hostfully.bookingservice.controller;

import com.hostfully.bookingservice.dto.BookingUpdateRequest;
import com.hostfully.bookingservice.model.Booking;
import com.hostfully.bookingservice.dto.BookingRequest;
import com.hostfully.bookingservice.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Booking> createBooking(@Valid @RequestBody BookingRequest request) {
        return bookingService.createBooking(request);
    }

    @GetMapping("/{id}")
    public Mono<Booking> getBooking(@PathVariable String id) {
        return bookingService.getBooking(id);
    }

    @PutMapping("/{id}")
    public Mono<Booking> updateBooking(@PathVariable String id,
                                       @Valid @RequestBody BookingUpdateRequest request) {
        return bookingService.updateBooking(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteBooking(@PathVariable String id) {
        return bookingService.deleteBooking(id);
    }

    @PostMapping("/{id}/cancel")
    public Mono<Booking> cancelBooking(@PathVariable String id) {
        return bookingService.cancelBooking(id);
    }

    @PostMapping("/{id}/rebook")
    public Mono<Booking> rebookBooking(@PathVariable String id) {
        return bookingService.rebookBooking(id);
    }
}
