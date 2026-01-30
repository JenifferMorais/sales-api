package com.sales.infrastructure.persistence.customer.service;

import com.sales.infrastructure.persistence.customer.repository.CustomerPanacheRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerCodeGenerator Tests")
class CustomerCodeGeneratorTest {

    @Mock
    private CustomerPanacheRepository repository;

    @InjectMocks
    private CustomerCodeGenerator codeGenerator;

    @Test
    @DisplayName("Should generate first code when no customers exist")
    void shouldGenerateFirstCodeWhenNoCustomersExist() {
        when(repository.findLastCode()).thenReturn(Optional.empty());

        String code = codeGenerator.generateNextCode();

        assertThat(code).isEqualTo("CUST0001");
    }

    @Test
    @DisplayName("Should generate first code when last code is null")
    void shouldGenerateFirstCodeWhenLastCodeIsNull() {
        when(repository.findLastCode()).thenReturn(Optional.ofNullable(null));

        String code = codeGenerator.generateNextCode();

        assertThat(code).isEqualTo("CUST0001");
    }

    @Test
    @DisplayName("Should generate first code when last code is blank")
    void shouldGenerateFirstCodeWhenLastCodeIsBlank() {
        when(repository.findLastCode()).thenReturn(Optional.of(""));

        String code = codeGenerator.generateNextCode();

        assertThat(code).isEqualTo("CUST0001");
    }

    @Test
    @DisplayName("Should generate next code incrementing last code")
    void shouldGenerateNextCodeIncrementingLastCode() {
        when(repository.findLastCode()).thenReturn(Optional.of("CUST0001"));

        String code = codeGenerator.generateNextCode();

        assertThat(code).isEqualTo("CUST0002");
    }

    @Test
    @DisplayName("Should generate code with correct padding")
    void shouldGenerateCodeWithCorrectPadding() {
        when(repository.findLastCode()).thenReturn(Optional.of("CUST0099"));

        String code = codeGenerator.generateNextCode();

        assertThat(code).isEqualTo("CUST0100");
    }

    @Test
    @DisplayName("Should generate code incrementing from four digit number")
    void shouldGenerateCodeIncrementingFromFourDigitNumber() {
        when(repository.findLastCode()).thenReturn(Optional.of("CUST9999"));

        String code = codeGenerator.generateNextCode();

        assertThat(code).isEqualTo("CUST10000");
    }

    @Test
    @DisplayName("Should handle invalid code format by using count")
    void shouldHandleInvalidCodeFormatByUsingCount() {
        when(repository.findLastCode()).thenReturn(Optional.of("INVALID"));
        when(repository.countAll()).thenReturn(5L);

        String code = codeGenerator.generateNextCode();

        assertThat(code).isEqualTo("CUST0006");
    }

    @Test
    @DisplayName("Should handle code with non-numeric suffix by using count")
    void shouldHandleCodeWithNonNumericSuffixByUsingCount() {
        when(repository.findLastCode()).thenReturn(Optional.of("CUSTABCD"));
        when(repository.countAll()).thenReturn(10L);

        String code = codeGenerator.generateNextCode();

        assertThat(code).isEqualTo("CUST0011");
    }

    @Test
    @DisplayName("Should handle parsing exception by using count")
    void shouldHandleParsingExceptionByUsingCount() {
        when(repository.findLastCode()).thenReturn(Optional.of("CUST"));
        when(repository.countAll()).thenReturn(3L);

        String code = codeGenerator.generateNextCode();

        assertThat(code).isEqualTo("CUST0004");
    }

    @Test
    @DisplayName("Should generate sequential codes")
    void shouldGenerateSequentialCodes() {
        when(repository.findLastCode())
                .thenReturn(Optional.of("CUST0001"))
                .thenReturn(Optional.of("CUST0002"))
                .thenReturn(Optional.of("CUST0003"));

        String code1 = codeGenerator.generateNextCode();
        String code2 = codeGenerator.generateNextCode();
        String code3 = codeGenerator.generateNextCode();

        assertThat(code1).isEqualTo("CUST0002");
        assertThat(code2).isEqualTo("CUST0003");
        assertThat(code3).isEqualTo("CUST0004");
    }

    @Test
    @DisplayName("Should maintain format CUST followed by 4 digits minimum")
    void shouldMaintainFormatWithFourDigitsMinimum() {
        when(repository.findLastCode()).thenReturn(Optional.of("CUST0001"));

        String code = codeGenerator.generateNextCode();

        assertThat(code).matches("CUST\\d{4,}");
    }

    @Test
    @DisplayName("Should use count as fallback when last code is corrupted")
    void shouldUseCountAsFallbackWhenLastCodeIsCorrupted() {
        when(repository.findLastCode()).thenReturn(Optional.of("CORRUPTED_CODE"));
        when(repository.countAll()).thenReturn(100L);

        String code = codeGenerator.generateNextCode();

        assertThat(code).isEqualTo("CUST0101");
    }
}
