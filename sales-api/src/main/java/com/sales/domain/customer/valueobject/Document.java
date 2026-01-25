package com.sales.domain.customer.valueobject;

import java.util.Objects;

public class Document {
    private final String cpf;
    private final String rg;

    public Document(String cpf, String rg) {
        this.cpf = validateCPF(cpf);
        this.rg = validateRG(rg);
    }

    public static Document create(String cpf, String rg) {
        return new Document(cpf, rg);
    }

    public static Document fromDatabase(String cpf, String rg) {
        return new Document(cpf, rg, false);
    }

    private Document(String cpf, String rg, boolean validate) {
        if (validate) {
            this.cpf = validateCPF(cpf);
            this.rg = validateRG(rg);
        } else {
            this.cpf = cpf != null ? cpf.replaceAll("[^0-9]", "") : cpf;
            this.rg = rg != null ? rg.replaceAll("[^0-9Xx]", "").toUpperCase() : rg;
        }
    }

    private String validateCPF(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("CPF não pode estar vazio");
        }
        String cleaned = cpf.replaceAll("[^0-9]", "");
        if (cleaned.length() != 11) {
            throw new IllegalArgumentException("CPF deve ter 11 dígitos");
        }
        if (!isValidCPF(cleaned)) {
            throw new IllegalArgumentException("CPF inválido");
        }
        return cleaned;
    }

    private String validateRG(String rg) {
        if (rg == null || rg.isBlank()) {
            throw new IllegalArgumentException("RG não pode estar vazio");
        }
        String cleaned = rg.replaceAll("[^0-9Xx]", "");
        if (cleaned.length() < 7 || cleaned.length() > 9) {
            throw new IllegalArgumentException("Formato de RG inválido");
        }
        return cleaned.toUpperCase();
    }

    private boolean isValidCPF(String cpf) {
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        int[] digits = cpf.chars().map(c -> c - '0').toArray();

        int sum1 = 0;
        for (int i = 0; i < 9; i++) {
            sum1 += digits[i] * (10 - i);
        }
        int check1 = 11 - (sum1 % 11);
        check1 = check1 >= 10 ? 0 : check1;

        int sum2 = 0;
        for (int i = 0; i < 10; i++) {
            sum2 += digits[i] * (11 - i);
        }
        int check2 = 11 - (sum2 % 11);
        check2 = check2 >= 10 ? 0 : check2;

        return digits[9] == check1 && digits[10] == check2;
    }

    public String getCpf() {
        return cpf;
    }

    public String getRg() {
        return rg;
    }

    public String getFormattedCPF() {
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." +
               cpf.substring(6, 9) + "-" + cpf.substring(9);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return Objects.equals(cpf, document.cpf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpf);
    }
}
