package com.hostfully.bookingservice.service;

import com.hostfully.bookingservice.dto.BlockRequest;
import com.hostfully.bookingservice.exception.OverlappingReservationException;
import com.hostfully.bookingservice.model.Block;
import com.hostfully.bookingservice.repository.BlockRepository;
import com.hostfully.bookingservice.repository.BookingRepository;
import com.hostfully.bookingservice.exception.NotFoundException;
import com.hostfully.bookingservice.util.DateChecker;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;

@Service
public class BlockService {

    private final BlockRepository blockRepository;
    private final BookingRepository bookingRepository;

    public BlockService(BookingRepository bookingRepository, BlockRepository blockRepository, DateChecker dateChecker) {
        this.bookingRepository = bookingRepository;
        this.blockRepository = blockRepository;
    }

    public Mono<Block> createBlock(BlockRequest request) {
        Block block = Block.builder()
                .propertyId(request.getPropertyId())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .reason(request.getReason())
                .build();
        return checkOverlap(block.getPropertyId(), block.getStartDate(), block.getEndDate(), null)
                .then(Mono.fromCallable(() -> blockRepository.save(block))
                        .subscribeOn(Schedulers.boundedElastic()));
    }

    public Mono<Block> updateBlock(Long id, BlockRequest request) {
        DateChecker.validateDates(request.getStartDate(), request.getEndDate());

        return Mono.fromCallable(() -> blockRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(o -> o.isPresent() ? Mono.just(o.get()) : Mono.empty())
                .switchIfEmpty(Mono.error(new NotFoundException(String.format("Block %d not found", id))))
                .flatMap(existing -> checkOverlap(request.getPropertyId(),
                        request.getStartDate(), request.getEndDate(), id)
                        .then(Mono.fromCallable(() -> {
                            existing.setPropertyId(request.getPropertyId());
                            existing.setStartDate(request.getStartDate());
                            existing.setEndDate(request.getEndDate());
                            existing.setReason(request.getReason());
                            return blockRepository.save(existing);
                        }).subscribeOn(Schedulers.boundedElastic())));
    }

    public Mono<Void> deleteBlock(Long id) {
        return Mono.fromRunnable(() -> blockRepository.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic()).then();
    }


    /**
     * Checks whether for a given property and [start, end) there exists
     *  - booking that intersects with [start, end)
     *  - block that intersects with [start, end)
     * @param propertyId
     * @param start
     * @param end
     * @param excludeBlockId
     * @return Mono.empty() if there is no overlap or Mono.error() if there is.
     */
    private Mono<Void> checkOverlap(Long propertyId, LocalDate start, LocalDate end, Long excludeBlockId) {

        Mono<Void> bookingCheck = Mono.fromCallable(() -> bookingRepository.checkOverlap(propertyId, start, end, null))
                .subscribeOn(Schedulers.boundedElastic())
                .filter(hasOverlap -> hasOverlap)
                .flatMap(b -> Mono.<Void>error(new OverlappingReservationException(
                        "Block overlaps with an existing active booking")));

        Mono<Void> blockCheck = Mono.fromCallable(() -> blockRepository.checkOverlap(propertyId, start, end, excludeBlockId))
                .subscribeOn(Schedulers.boundedElastic())
                .filter(hasOverlap -> hasOverlap)
                .flatMap(b -> Mono.<Void>error(new OverlappingReservationException(
                        "Block overlaps with an existing block")));

        return Mono.when(bookingCheck, blockCheck);
    }
}
