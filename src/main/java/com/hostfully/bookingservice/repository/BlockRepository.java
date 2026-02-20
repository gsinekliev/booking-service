package com.hostfully.bookingservice.repository;


import com.hostfully.bookingservice.model.Block;
import com.hostfully.bookingservice.util.DateChecker;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface BlockRepository extends JpaRepository<Block, Long> {
    List<Block> findAllByPropertyId(Long propertyId);

    default boolean checkOverlap(Long propertyId, LocalDate startDate, LocalDate endDate, Long excludeBlockId) {
        return this.findAllByPropertyId(propertyId)
                .stream().filter(b -> excludeBlockId == null || !b.getId().equals(excludeBlockId))
                .anyMatch(b -> DateChecker.datesOverlap(startDate, endDate, b.getStartDate(), b.getEndDate()));

    }
}
