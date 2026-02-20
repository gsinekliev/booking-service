package com.hostfully.bookingservice.exception;

public class OverlappingReservationException extends RuntimeException {
    public OverlappingReservationException(String s) {
        super(s);
    }
}
