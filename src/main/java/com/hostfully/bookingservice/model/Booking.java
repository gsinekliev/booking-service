package com.hostfully.bookingservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class Booking {
    private String id;
    private String propertyId;
    private Guest guest;
    private LocalDate startDate;
    private LocalDate endDate;
    private BookingStatus status;
}
