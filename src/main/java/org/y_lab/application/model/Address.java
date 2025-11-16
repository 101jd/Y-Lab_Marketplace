package org.y_lab.application.model;

import org.y_lab.application.model.dto.AddressDTO;

import java.util.UUID;

/**
 * Domain class Address
 */
public class Address {
    private UUID id;
    private String city;
    private String street;
    private int houseNumber;
    private int apartment;

    /**
     * Constructor from AddressDTO object
     * @param dto to copy fields
     */
    public Address(AddressDTO dto){
        this.id = dto.getId();
        this.city = dto.getCity();
        this.street = dto.getStreet();
        this.houseNumber = dto.getHouseNumber();
        this.apartment = dto.getApartment();
    }

    /**
     * Base constructor
     * @param city
     * @param street
     * @param houseNumber
     * @param appartment
     */
    public Address(String city, String street, int houseNumber, int appartment) {
        this.id = UUID.randomUUID();
        this.city = city;
        this.street = street;
        this.houseNumber = houseNumber;
        this.apartment = appartment;
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
