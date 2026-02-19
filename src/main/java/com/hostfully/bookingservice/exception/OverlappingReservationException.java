package com.hostfully.bookingservice.exception;

import java.util.function.Supplier;

public class OverlappingReservationException extends RuntimeException {
    public OverlappingReservationException(String s) {
        super(s);
    }
}
