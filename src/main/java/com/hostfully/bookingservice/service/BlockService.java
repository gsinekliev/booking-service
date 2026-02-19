package com.hostfully.bookingservice.service;

import com.hostfully.bookingservice.dto.BlockRequest;
import com.hostfully.bookingservice.model.Block;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
public class BlockService {
    public BlockService() {

    }

    public Mono<Block> createBlock(BlockRequest request) {
        Block block = Block.builder()
                .propertyId(request.getPropertyId())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .reason(request.getReason())
                .build();
        return checkOverlap(block.getPropertyId(), block.getStartDate(), block.getEndDate(), null)
                .then(Mono.just(block));
    }

    public Mono<Block> updateBlock(String id,
                                   BlockRequest request) {
        return Mono.empty();
    }

    public Mono<Void> deleteBlock(String id) {
        return Mono.empty();
    }

    private Mono<Void> checkOverlap(String propertyId,
                                    LocalDate start,
                                    LocalDate end,
                                    String excludeBlockId) {
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
