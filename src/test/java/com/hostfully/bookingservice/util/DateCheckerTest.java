package com.hostfully.bookingservice.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class DateCheckerTest {

    DateChecker dateChecker;

    @BeforeEach
    void setUp() {
        dateChecker = new DateChecker();
    }

    @Test
    void testOverlaps() {
        LocalDate s1 = LocalDate.of(2025, 1, 5);
        LocalDate e1 = LocalDate.of(2025, 1, 10);
        LocalDate s2 = LocalDate.of(2025, 1, 7);
        LocalDate e2 = LocalDate.of(2025, 1, 12);

        Assertions.assertTrue(e1.isAfter(s1));
        Assertions.assertTrue(e2.isAfter(s2));
        Assertions.assertTrue(dateChecker.datesOverlap(s1, e1, s2, e2));
        Assertions.assertTrue(dateChecker.datesOverlap(s2, e2, s1, e1));
    }

    @Test
    void testOverlapsIntervalInclusion() {
        LocalDate s1 = LocalDate.of(2025, 1, 5);
        LocalDate e1 = LocalDate.of(2025, 1, 10);
        LocalDate s2 = LocalDate.of(2025, 1, 1);
        LocalDate e2 = LocalDate.of(2025, 1, 12);

        Assertions.assertTrue(e1.isAfter(s1));
        Assertions.assertTrue(e2.isAfter(s2));
        Assertions.assertTrue(dateChecker.datesOverlap(s1, e1, s2, e2));
        Assertions.assertTrue(dateChecker.datesOverlap(s2, e2, s1, e1));
    }

}