package com.sales.domain.customer.entity;

import com.sales.domain.customer.valueobject.Address;
import com.sales.domain.customer.valueobject.Document;
import com.sales.domain.shared.Entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Customer extends Entity {
    private String fullName;
    private String motherName;
    private Document document;
    private Address address;
    private LocalDate birthDate;
    private String cellPhone;
    private String email;

    public Customer(String code, String fullName, String motherName, Document document,
                   Address address, LocalDate birthDate, String cellPhone, String email) {
        super();

        this.code = code;
        this.fullName = validateNotEmpty(fullName, "Nome completo não pode estar vazio");
        this.motherName = validateNotEmpty(motherName, "Nome da mãe não pode estar vazio");
        this.document = Objects.requireNonNull(document, "Documento não pode ser nulo");
        this.address = Objects.requireNonNull(address, "Endereço não pode ser nulo");
        this.birthDate = validateBirthDate(birthDate);
        this.cellPhone = validateCellPhone(cellPhone);
        this.email = validateEmail(email);
    }

    public static Customer create(String code, String fullName, String motherName, Document document,
                                  Address address, LocalDate birthDate, String cellPhone, String email) {
        return new Customer(code, fullName, motherName, document, address, birthDate, cellPhone, email);
    }

    public Customer(Long id, String code, String fullName, String motherName, Document document,
                   Address address, LocalDate birthDate, String cellPhone, String email,
                   LocalDateTime createdAt) {
        super(id, code, createdAt);
        this.fullName = validateNotEmpty(fullName, "Nome completo não pode estar vazio");
        this.motherName = validateNotEmpty(motherName, "Nome da mãe não pode estar vazio");
        this.document = Objects.requireNonNull(document, "Documento não pode ser nulo");
        this.address = Objects.requireNonNull(address, "Endereço não pode ser nulo");
        this.birthDate = validateBirthDate(birthDate);
        this.cellPhone = validateCellPhone(cellPhone);
        this.email = validateEmail(email);
    }

    private String validateNotEmpty(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    private LocalDate validateBirthDate(LocalDate birthDate) {
        if (birthDate == null) {
            throw new IllegalArgumentException("Data de nascimento não pode ser nula");
        }
        if (birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de nascimento não pode ser no futuro");
        }
        if (birthDate.isBefore(LocalDate.now().minusYears(150))) {
            throw new IllegalArgumentException("Data de nascimento inválida");
        }
        return birthDate;
    }

    private String validateCellPhone(String cellPhone) {
        if (cellPhone == null || cellPhone.isBlank()) {
            throw new IllegalArgumentException("Celular não pode estar vazio");
        }
        String cleaned = cellPhone.replaceAll("[^0-9]", "");
        if (cleaned.length() != 11) {
            throw new IllegalArgumentException("Celular deve ter 11 dígitos");
        }
        return cleaned;
    }

    private String validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email não pode estar vazio");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Formato de email inválido");
        }
        return email.toLowerCase();
    }

    public void updateInfo(String fullName, String motherName, Address address,
                          LocalDate birthDate, String cellPhone, String email) {
        this.fullName = validateNotEmpty(fullName, "Nome completo não pode estar vazio");
        this.motherName = validateNotEmpty(motherName, "Nome da mãe não pode estar vazio");
        this.address = Objects.requireNonNull(address, "Endereço não pode ser nulo");
        this.birthDate = validateBirthDate(birthDate);
        this.cellPhone = validateCellPhone(cellPhone);
        this.email = validateEmail(email);
    }

    public String getFullName() {
        return fullName;
    }

    public String getMotherName() {
        return motherName;
    }

    public Document getDocument() {
        return document;
    }

    public Address getAddress() {
        return address;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public String getFormattedCellPhone() {
        return "(" + cellPhone.substring(0, 2) + ") " +
               cellPhone.substring(2, 7) + "-" + cellPhone.substring(7);
    }

    public String getEmail() {
        return email;
    }
}
