package com.hostfully.bookingservice.controller;

import com.hostfully.bookingservice.dto.BlockRequest;
import com.hostfully.bookingservice.model.Block;
import com.hostfully.bookingservice.service.BlockService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/blocks")
public class BlockController {

    private final BlockService blockService;

    public BlockController(BlockService blockService) {
        this.blockService = blockService;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Block> createBlock(@Valid @RequestBody BlockRequest request) {
        return blockService.createBlock(request);
    }

    @PutMapping("/{id}")
    public Mono<Block> updateBlock(@PathVariable String id,
                                   @Valid @RequestBody BlockRequest request) {
        return blockService.updateBlock(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteBlock(@PathVariable String id) {
        return blockService.deleteBlock(id);
    }
}
