package com.sales.domain.customer.valueobject;

import java.util.Objects;

public class Address {
    private final String zipCode;
    private final String street;
    private final String number;
    private final String complement;
    private final String neighborhood;
    private final String city;
    private final String state;

    public Address(String zipCode, String street, String number, String complement,
                   String neighborhood, String city, String state) {
        this.zipCode = validateZipCode(zipCode);
        this.street = validateNotEmpty(street, "Rua não pode estar vazia");
        this.number = validateNotEmpty(number, "Número não pode estar vazio");
        this.complement = complement;
        this.neighborhood = validateNotEmpty(neighborhood, "Bairro não pode estar vazio");
        this.city = validateNotEmpty(city, "Cidade não pode estar vazia");
        this.state = validateState(state);
    }

    public static Address create(String zipCode, String street, String number, String complement,
                                  String neighborhood, String city, String state) {
        return new Address(zipCode, street, number, complement, neighborhood, city, state);
    }

    private String validateZipCode(String zipCode) {
        if (zipCode == null || zipCode.isBlank()) {
            throw new IllegalArgumentException("CEP não pode estar vazio");
        }
        String cleaned = zipCode.replaceAll("[^0-9]", "");
        if (cleaned.length() != 8) {
            throw new IllegalArgumentException("CEP deve ter 8 dígitos");
        }
        return cleaned;
    }

    private String validateState(String state) {
        if (state == null || state.isBlank()) {
            throw new IllegalArgumentException("Estado não pode estar vazio");
        }
        if (state.length() != 2) {
            throw new IllegalArgumentException("Estado deve ter 2 caracteres");
        }
        return state.toUpperCase();
    }

    private String validateNotEmpty(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getStreet() {
        return street;
    }

    public String getNumber() {
        return number;
    }

    public String getComplement() {
        return complement;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getFormattedZipCode() {
        return zipCode.substring(0, 5) + "-" + zipCode.substring(5);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(zipCode, address.zipCode) &&
               Objects.equals(street, address.street) &&
               Objects.equals(number, address.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(zipCode, street, number);
    }

    @Override
    public String toString() {
        return String.format("%s, %s%s - %s, %s/%s - CEP: %s",
                street, number,
                complement != null && !complement.isBlank() ? " (" + complement + ")" : "",
                neighborhood, city, state, getFormattedZipCode());
    }
}
