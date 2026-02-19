package com.hostfully.bookingservice.service;

import com.hostfully.bookingservice.dto.BookingRequest;
import com.hostfully.bookingservice.dto.BookingUpdateRequest;
import com.hostfully.bookingservice.model.Booking;
import com.hostfully.bookingservice.model.BookingStatus;
import com.hostfully.bookingservice.model.Guest;
import com.hostfully.bookingservice.repository.BookingRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public Mono<Booking> createBooking(BookingRequest request) {
        validateDates(request.getStartDate(), request.getEndDate());

        Guest guest = Guest.builder()
                .firstName(request.getGuestName())
                .email(request.getGuestEmail())
                .build();

        Booking booking = Booking.builder()
                .propertyId(request.getPropertyId())
                .guest(guest)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(BookingStatus.ACTIVE)
                .build();

        bookingRepository.save(booking);
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
