package com.hostfully.bookingservice.repository;

import com.hostfully.bookingservice.model.Booking;
import com.hostfully.bookingservice.model.BookingStatus;
import com.hostfully.bookingservice.util.DateChecker;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByPropertyId(Long propertyId);

    default boolean checkOverlap(Long propertyId, LocalDate startDate, LocalDate endDate, Long excludeBookingId) {
        return this.findAllByPropertyId(propertyId)
                .stream()
                .filter(b -> b.getStatus() == BookingStatus.ACTIVE)
                .filter(b -> excludeBookingId == null || !b.getId().equals(excludeBookingId))
                .anyMatch(b -> DateChecker.datesOverlap(startDate, endDate, b.getStartDate(), b.getEndDate()));
    }
}
