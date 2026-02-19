package com.hostfully.bookingservice.service;

import com.hostfully.bookingservice.dto.BookingRequest;
import com.hostfully.bookingservice.dto.BookingUpdateRequest;
import com.hostfully.bookingservice.exception.OverlappingReservationException;
import com.hostfully.bookingservice.model.Booking;
import com.hostfully.bookingservice.model.BookingStatus;
import com.hostfully.bookingservice.model.Guest;
import com.hostfully.bookingservice.repository.BlockRepository;
import com.hostfully.bookingservice.repository.BookingRepository;
import com.hostfully.bookingservice.service.exception.NotFoundException;
import com.hostfully.bookingservice.util.DateChecker;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final DateChecker dateChecker;
    private final BlockRepository blockRepository;

    public BookingService(BookingRepository bookingRepository,
                          BlockRepository blockRepository,
                          DateChecker dateChecker) {
        this.bookingRepository = bookingRepository;
        this.blockRepository = blockRepository;
        this.dateChecker = dateChecker;
    }

    public Mono<Booking> createBooking(BookingRequest request) {

        dateChecker.validateDates(request.getStartDate(), request.getEndDate());

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

        return Mono.fromCallable(() -> bookingRepository.save(booking))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Booking> getBooking(Long id) {
        return Mono.fromCallable(() -> bookingRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(Mono::justOrEmpty);
    }

    public Mono<Booking> updateBooking(Long id, BookingUpdateRequest request) {
        dateChecker.validateDates(request.getStartDate(), request.getEndDate());
        Mono<Booking> result = Mono.fromCallable(() -> bookingRepository.findById(id)).subscribeOn(Schedulers.boundedElastic()).flatMap(Mono::justOrEmpty);

        return result
                .switchIfEmpty(Mono.error(new NotFoundException("Booking not found")))
                .flatMap(existing -> {
                    if (existing.getStatus() == BookingStatus.CANCELED) {
                        return Mono.error(new IllegalStateException("Cannot update a cancelled booking"));
                    }

                    return checkOverlap(existing.getPropertyId(), existing.getStartDate(), existing.getEndDate(), existing.getId()).then(Mono.fromCallable(() -> {
                        if (request.getStartDate() != null) {
                            existing.setStartDate(request.getStartDate());
                        }

                        if (request.getEndDate() != null) {
                            existing.setEndDate(request.getEndDate());
                        }

                        return bookingRepository.save(existing);
                    }));
                });
    }

    public Mono<Booking> cancelBooking(Long id) {
        return Mono.empty();
    }

    public Mono<Booking> rebookBooking(Long id) {
        return Mono.empty();
    }

    public Mono<Void> deleteBooking(Long id) {
        return Mono.fromRunnable(() -> bookingRepository.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    /**
     * Checks that no ACTIVE booking or block overlaps with the given date range on the property.
     * Uses exclusive end-date semantics: [start, end) — so a booking ending on March 10
     * does not conflict with one starting on March 10.
     */
    private Mono<Void> checkOverlap(Long propertyId, LocalDate start, LocalDate end,
                                    Long excludeBookingId) {
        Mono<Void> bookingCheck = Mono.fromCallable(() -> bookingRepository.findAllByPropertyId(propertyId))
                .flatMapMany(Flux::fromIterable)
                .filter(b -> b.getStatus() == BookingStatus.ACTIVE)
                .filter(b -> excludeBookingId == null || !b.getId().equals(excludeBookingId))
                .filter(b -> dateChecker.datesOverlap(start, end, b.getStartDate(), b.getEndDate()))
                .next()
                .flatMap(b -> Mono.<Void>error(new OverlappingReservationException(
                        "Booking overlaps with an existing booking")));

        Mono<Void> blockCheck = Mono.fromCallable(() -> blockRepository.findAllByPropertyId(propertyId))
                .flatMapMany(Flux::fromIterable)
                .filter(b -> dateChecker.datesOverlap(start, end, b.getStartDate(), b.getEndDate()))
                .next()
                .flatMap(b -> Mono.<Void>error(new OverlappingReservationException(
                        "Booking overlaps with a block")));

        return bookingCheck.then(blockCheck);
    }
}
