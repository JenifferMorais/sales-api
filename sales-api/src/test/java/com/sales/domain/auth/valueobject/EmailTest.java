package com.sales.domain.auth.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class EmailTest {

    @Test
    void shouldCreateValidEmail() {
        Email email = new Email("test@example.com");

        assertThat(email.getValue()).isEqualTo("test@example.com");
    }

    @Test
    void shouldConvertToLowerCase() {
        Email email = new Email("TEST@EXAMPLE.COM");

        assertThat(email.getValue()).isEqualTo("test@example.com");
    }

    @Test
    void shouldCreateEmailWithCreateMethod() {
        Email email = Email.create("test@example.com");

        assertThat(email.getValue()).isEqualTo("test@example.com");
    }

    @Test
    void shouldFailWhenEmailIsNull() {
        assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email não pode estar vazio");
    }

    @Test
    void shouldFailWhenEmailIsEmpty() {
        assertThatThrownBy(() -> new Email(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email não pode estar vazio");
    }

    @Test
    void shouldFailWhenEmailIsBlank() {
        assertThatThrownBy(() -> new Email("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email não pode estar vazio");
    }

    @Test
    void shouldFailWhenEmailHasInvalidFormat() {
        assertThatThrownBy(() -> new Email("invalid-email"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Formato de email inválido");
    }

    @Test
    void shouldFailWhenEmailMissingAtSign() {
        assertThatThrownBy(() -> new Email("testexample.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Formato de email inválido");
    }

    @Test
    void shouldFailWhenEmailMissingDomain() {
        assertThatThrownBy(() -> new Email("test@"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Formato de email inválido");
    }

    @Test
    void shouldFailWhenEmailMissingTLD() {
        assertThatThrownBy(() -> new Email("test@example"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Formato de email inválido");
    }

    @Test
    void shouldAcceptEmailWithPlusSign() {
        Email email = new Email("test+tag@example.com");

        assertThat(email.getValue()).isEqualTo("test+tag@example.com");
    }

    @Test
    void shouldAcceptEmailWithDot() {
        Email email = new Email("test.name@example.com");

        assertThat(email.getValue()).isEqualTo("test.name@example.com");
    }

    @Test
    void shouldAcceptEmailWithUnderscore() {
        Email email = new Email("test_name@example.com");

        assertThat(email.getValue()).isEqualTo("test_name@example.com");
    }

    @Test
    void shouldAcceptEmailWithHyphen() {
        Email email = new Email("test-name@example.com");

        assertThat(email.getValue()).isEqualTo("test-name@example.com");
    }

    @Test
    void shouldBeEqualWhenSameValue() {
        Email email1 = new Email("test@example.com");
        Email email2 = new Email("test@example.com");

        assertThat(email1).isEqualTo(email2);
        assertThat(email1.hashCode()).isEqualTo(email2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenDifferentValue() {
        Email email1 = new Email("test1@example.com");
        Email email2 = new Email("test2@example.com");

        assertThat(email1).isNotEqualTo(email2);
    }

    @Test
    void shouldReturnValueInToString() {
        Email email = new Email("test@example.com");

        assertThat(email.toString()).isEqualTo("test@example.com");
    }
}
