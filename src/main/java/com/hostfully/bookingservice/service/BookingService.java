package com.hostfully.bookingservice.service;

import com.hostfully.bookingservice.dto.BookingRequest;
import com.hostfully.bookingservice.dto.BookingUpdateRequest;
import com.hostfully.bookingservice.model.Booking;
import com.hostfully.bookingservice.model.BookingStatus;
import com.hostfully.bookingservice.model.Guest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class BookingService {

    public BookingService() {
    }

    public Mono<Booking> createBooking(BookingRequest request) {
        validateDates(request.getStartDate(), request.getEndDate());
        Guest guest = new Guest();
        guest.setId(UUID.randomUUID().toString());
        guest.setFirstName(request.getGuestName());
        guest.setEmail(request.getGuestEmail());

        Booking booking = new Booking(
                UUID.randomUUID().toString(),
                request.getPropertyId(),
                guest,
                request.getStartDate(),
                request.getEndDate(),
                BookingStatus.ACTIVE
        );
        return Mono.just(booking);
    }

    public Mono<Booking> getBooking(String id) {
        return Mono.empty();
    }

    public Mono<Booking> updateBooking(String id, BookingUpdateRequest request) {
        return Mono.empty();
    }

    public Mono<Booking> cancelBooking(String id) {
        return Mono.empty();
    }

    public Mono<Booking> rebookBooking(String id) {
        return Mono.empty();
    }

    public Mono<Void> deleteBooking(String id) {
        return Mono.empty();
    }

    private boolean datesOverlap(LocalDate s1, LocalDate e1, LocalDate s2, LocalDate e2) {
        return s1.isBefore(e2) && s2.isBefore(e1);
    }

    private void validateDates(LocalDate start, LocalDate end) {
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
    }
}
