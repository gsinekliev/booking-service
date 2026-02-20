package com.hostfully.bookingservice.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DateChecker {
    public DateChecker() {

    }

    public static boolean datesOverlap(LocalDate s1, LocalDate e1, LocalDate s2, LocalDate e2) {
        if (s2.isBefore(s1)) {
            // make sure s1 is before s2
            LocalDate tmpS = s1;
            s1 = s2;
            s2 = tmpS;

            LocalDate tmpE = e1;
            e1 = e2;
            e2 = tmpE;
        }

        return ((s2.isAfter(s1) || s2.equals(s1)) && s2.isBefore(e1));
    }

    public static void validateDates(LocalDate start, LocalDate end) {
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
    }
}
