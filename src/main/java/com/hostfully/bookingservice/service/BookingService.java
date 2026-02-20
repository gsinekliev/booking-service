package com.hostfully.bookingservice.service;

import com.hostfully.bookingservice.dto.BookingRequest;
import com.hostfully.bookingservice.dto.BookingUpdateRequest;
import com.hostfully.bookingservice.exception.OverlappingReservationException;
import com.hostfully.bookingservice.model.Booking;
import com.hostfully.bookingservice.model.BookingStatus;
import com.hostfully.bookingservice.model.Guest;
import com.hostfully.bookingservice.repository.BlockRepository;
import com.hostfully.bookingservice.repository.BookingRepository;
import com.hostfully.bookingservice.repository.GuestRepository;
import com.hostfully.bookingservice.exception.NotFoundException;
import com.hostfully.bookingservice.util.DateChecker;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import java.time.LocalDate;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BlockRepository blockRepository;
    private final GuestRepository guestRepository;

    public BookingService(BookingRepository bookingRepository,
                          BlockRepository blockRepository,
                          GuestRepository guestRepository,
                          DateChecker dateChecker) {
        this.bookingRepository = bookingRepository;
        this.blockRepository = blockRepository;
        this.guestRepository = guestRepository;
    }

    public Mono<Booking> createBooking(BookingRequest request) {

        DateChecker.validateDates(request.getStartDate(), request.getEndDate());

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

        return checkOverlap(request.getPropertyId(), request.getStartDate(), request.getEndDate(), booking.getId()).then(
                Mono.fromCallable(() -> {
                    guestRepository.save(guest);
                    return bookingRepository.save(booking);
                }).subscribeOn(Schedulers.boundedElastic())
        );
    }

    public Mono<Booking> getBooking(Long id) {
        return Mono.fromCallable(() -> bookingRepository.findById(id))
                .flatMap(Mono::justOrEmpty)
                .switchIfEmpty(Mono.error(new NotFoundException("Booking not found with id: " + id)))
                .subscribeOn(Schedulers.boundedElastic());

    }

    public Mono<Booking> updateBooking(Long id, BookingUpdateRequest request) {
        DateChecker.validateDates(request.getStartDate(), request.getEndDate());
        Mono<Booking> result = Mono.fromCallable(() -> bookingRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(Mono::justOrEmpty);

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
        Mono<Booking> result = Mono.fromCallable(() -> bookingRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(Mono::justOrEmpty);
        return result
                .switchIfEmpty(Mono.error(new NotFoundException("Booking not found")))
                .flatMap(booking -> {
                    booking.setStatus(BookingStatus.CANCELED);
                    return Mono.fromCallable(() -> bookingRepository.save(booking));
                });
    }

    public Mono<Booking> rebookBooking(Long id) {
        return Mono.fromCallable(() -> bookingRepository.findById(id)
                .stream()
                .filter(booking -> booking.getStatus().equals(BookingStatus.CANCELED))
                .filter(booking -> !bookingRepository.checkOverlap(booking.getPropertyId(), booking.getStartDate(), booking.getEndDate(), booking.getId()))
                .map(booking -> {
                    booking.setStatus(BookingStatus.ACTIVE);
                    return bookingRepository.save(booking);
                })
                .findFirst())
                .flatMap(Mono::justOrEmpty)
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> deleteBooking(Long id) {
        return Mono.fromRunnable(() -> {
            bookingRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Booking %d not found", id)));
            bookingRepository.deleteById(id);
        });
    }

    private Mono<Void> checkOverlap(Long propertyId, LocalDate start, LocalDate end,
                                    Long excludeBookingId) {
        Mono<Void> bookingCheck = Mono.fromCallable(() -> bookingRepository.checkOverlap(propertyId, start, end, excludeBookingId)).subscribeOn(Schedulers.boundedElastic())
                .filter(hasOverlap -> hasOverlap)
                .flatMap(b -> Mono.<Void>error(new OverlappingReservationException(
                        "Booking overlaps with an existing booking")));

        Mono<Void> blockCheck = Mono.fromCallable(() -> blockRepository.checkOverlap(propertyId, start, end, null)).subscribeOn(Schedulers.boundedElastic())
                .filter(hasOverlap -> hasOverlap)
                .flatMap(b -> Mono.<Void>error(new OverlappingReservationException(
                        "Booking overlaps with a block")));

        return bookingCheck.then(blockCheck);
    }
}
