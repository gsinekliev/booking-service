package com.hostfully.bookingservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Guest {
    private String id;
    private String firstName;
    private String lastName;
    private String documentId;
    private String email;
    private String addressLine;
    private String city;
}