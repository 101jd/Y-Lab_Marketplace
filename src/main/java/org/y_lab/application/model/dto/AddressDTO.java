package org.y_lab.application.model.dto;

import java.util.UUID;

public class AddressDTO {
    private UUID id;
    private String city;
    private String street;
    private int houseNumber;
    private int apartment;

    public AddressDTO(UUID id, String city, String street, int houseNumber, int apartment) {
        this.id = id;
        this.city = city;
        this.street = street;
        this.houseNumber = houseNumber;
        this.apartment = apartment;
    }

    public UUID getId() {
        return id;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public int getHouseNumber() {
        return houseNumber;
    }

    public int getApartment() {
        return apartment;
    }
}
