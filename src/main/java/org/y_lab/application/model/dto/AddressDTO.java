package org.y_lab.application.model.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class AddressDTO {
    @Nullable
    private UUID id;
    @NotNull
    @NotEmpty
    private String city;
    @NotNull
    @NotEmpty
    private String street;
    @NotNull
    private int houseNumber;
    @Nullable
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
