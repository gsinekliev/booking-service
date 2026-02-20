package com.hostfully.bookingservice.controller;

import com.hostfully.bookingservice.dto.BlockRequest;
import com.hostfully.bookingservice.model.Block;
import com.hostfully.bookingservice.service.BlockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/blocks")
@Tag(name = "Blocks", description = "Property block management")
public class BlockController {

    private final BlockService blockService;

    public BlockController(BlockService blockService) {
        this.blockService = blockService;
    }

    @Operation(summary = "Create a block on a property")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Block created"),
            @ApiResponse(responseCode = "400", description = "Invalid request or overlapping reservation"),
    })
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Block> createBlock(@Valid @RequestBody BlockRequest request) {
        return blockService.createBlock(request);
    }

    @Operation(summary = "Update a block")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Block updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request or overlapping reservation"),
            @ApiResponse(responseCode = "404", description = "Block not found"),
    })
    @PutMapping("/{id}")
    public Mono<Block> updateBlock(@Parameter(description = "Block ID") @PathVariable Long id,
                                   @Valid @RequestBody BlockRequest request) {
        return blockService.updateBlock(id, request);
    }

    @Operation(summary = "Delete a block")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Block deleted"),
            @ApiResponse(responseCode = "404", description = "Block not found"),
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteBlock(@Parameter(description = "Block ID") @PathVariable Long id) {
        return blockService.deleteBlock(id);
    }
}
