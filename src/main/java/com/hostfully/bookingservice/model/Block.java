package com.hostfully.bookingservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor()
public class Block {
    private String id;
    private String propertyId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
}