package com.sales.infrastructure.security;

import com.sales.domain.shared.exception.EncryptionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AesEncryptionService")
class AesEncryptionServiceTest {

    private AesEncryptionService encryptionService;
    private static final String TEST_KEY = generateTestKey();

    private static String generateTestKey() {
        return AesEncryptionService.generateNewKey();
    }

    @BeforeEach
    void setUp() {
        encryptionService = new AesEncryptionService(TEST_KEY);
    }

    @Nested
    @DisplayName("Inicialização")
    class Initialization {

        @Test
        @DisplayName("Deve inicializar com chave válida")
        void shouldInitializeWithValidKey() {
            assertDoesNotThrow(() -> new AesEncryptionService(TEST_KEY));
        }

        @Test
        @DisplayName("Deve lançar exceção quando chave é nula")
        void shouldThrowExceptionWhenKeyIsNull() {
            EncryptionException exception = assertThrows(
                EncryptionException.class,
                () -> new AesEncryptionService(null)
            );

            assertTrue(exception.getMessage().contains("não configurada"));
        }

        @Test
        @DisplayName("Deve lançar exceção quando chave é vazia")
        void shouldThrowExceptionWhenKeyIsEmpty() {
            EncryptionException exception = assertThrows(
                EncryptionException.class,
                () -> new AesEncryptionService("")
            );

            assertTrue(exception.getMessage().contains("não configurada"));
        }

        @Test
        @DisplayName("Deve lançar exceção quando chave tem tamanho incorreto")
        void shouldThrowExceptionWhenKeyHasInvalidSize() {
            // Gerar chave de 128 bits (16 bytes) ao invés de 256 bits
            byte[] invalidKey = new byte[16];
            String invalidKeyBase64 = Base64.getEncoder().encodeToString(invalidKey);

            EncryptionException exception = assertThrows(
                EncryptionException.class,
                () -> new AesEncryptionService(invalidKeyBase64)
            );

            assertTrue(exception.getMessage().contains("256 bits"));
        }

        @Test
        @DisplayName("Deve lançar exceção quando Base64 é inválido")
        void shouldThrowExceptionWhenBase64IsInvalid() {
            EncryptionException exception = assertThrows(
                EncryptionException.class,
                () -> new AesEncryptionService("not-valid-base64!!!")
            );

            assertTrue(exception.getMessage().contains("Base64 malformado"));
        }
    }

    @Nested
    @DisplayName("Criptografia")
    class Encryption {

        @Test
        @DisplayName("Deve criptografar texto com sucesso")
        void shouldEncryptTextSuccessfully() {
            String plaintext = "4532015112830366";

            String encrypted = encryptionService.encrypt(plaintext);

            assertNotNull(encrypted);
            assertFalse(encrypted.isEmpty());
            assertNotEquals(plaintext, encrypted);
        }

        @Test
        @DisplayName("Deve retornar Base64 válido")
        void shouldReturnValidBase64() {
            String plaintext = "4532015112830366";

            String encrypted = encryptionService.encrypt(plaintext);

            assertDoesNotThrow(() -> Base64.getDecoder().decode(encrypted));
        }

        @Test
        @DisplayName("Deve gerar ciphertexts diferentes para mesmo plaintext")
        void shouldGenerateDifferentCiphertextsForSamePlaintext() {
            String plaintext = "4532015112830366";

            String encrypted1 = encryptionService.encrypt(plaintext);
            String encrypted2 = encryptionService.encrypt(plaintext);

            assertNotEquals(encrypted1, encrypted2,
                "IVs diferentes devem produzir ciphertexts diferentes");
        }

        @Test
        @DisplayName("Deve criptografar textos de tamanhos variados")
        void shouldEncryptTextsOfVariousSizes() {
            assertDoesNotThrow(() -> encryptionService.encrypt("12"));
            assertDoesNotThrow(() -> encryptionService.encrypt("1234567890123456"));
            assertDoesNotThrow(() -> encryptionService.encrypt("12345678901234567890"));
            assertDoesNotThrow(() -> encryptionService.encrypt("A".repeat(100)));
        }

        @Test
        @DisplayName("Deve criptografar caracteres especiais")
        void shouldEncryptSpecialCharacters() {
            String plaintext = "!@#$%^&*()_+-={}[]|:;<>?,./~`";

            String encrypted = encryptionService.encrypt(plaintext);

            assertNotNull(encrypted);
            String decrypted = encryptionService.decrypt(encrypted);
            assertEquals(plaintext, decrypted);
        }

        @Test
        @DisplayName("Deve criptografar caracteres Unicode")
        void shouldEncryptUnicodeCharacters() {
            String plaintext = "Crédito 信用卡 🎉";

            String encrypted = encryptionService.encrypt(plaintext);

            assertNotNull(encrypted);
            String decrypted = encryptionService.decrypt(encrypted);
            assertEquals(plaintext, decrypted);
        }

        @Test
        @DisplayName("Deve lançar exceção ao criptografar null")
        void shouldThrowExceptionWhenEncryptingNull() {
            EncryptionException exception = assertThrows(
                EncryptionException.class,
                () -> encryptionService.encrypt(null)
            );

            assertTrue(exception.getMessage().contains("nulo ou vazio"));
        }

        @Test
        @DisplayName("Deve lançar exceção ao criptografar string vazia")
        void shouldThrowExceptionWhenEncryptingEmpty() {
            EncryptionException exception = assertThrows(
                EncryptionException.class,
                () -> encryptionService.encrypt("")
            );

            assertTrue(exception.getMessage().contains("nulo ou vazio"));
        }
    }

    @Nested
    @DisplayName("Descriptografia")
    class Decryption {

        @Test
        @DisplayName("Deve descriptografar com sucesso")
        void shouldDecryptSuccessfully() {
            String plaintext = "4532015112830366";
            String encrypted = encryptionService.encrypt(plaintext);

            String decrypted = encryptionService.decrypt(encrypted);

            assertEquals(plaintext, decrypted);
        }

        @Test
        @DisplayName("Deve descriptografar múltiplas vezes consistentemente")
        void shouldDecryptMultipleTimesConsistently() {
            String plaintext = "4532015112830366";
            String encrypted = encryptionService.encrypt(plaintext);

            String decrypted1 = encryptionService.decrypt(encrypted);
            String decrypted2 = encryptionService.decrypt(encrypted);
            String decrypted3 = encryptionService.decrypt(encrypted);

            assertEquals(plaintext, decrypted1);
            assertEquals(plaintext, decrypted2);
            assertEquals(plaintext, decrypted3);
        }

        @Test
        @DisplayName("Deve lançar exceção ao descriptografar null")
        void shouldThrowExceptionWhenDecryptingNull() {
            EncryptionException exception = assertThrows(
                EncryptionException.class,
                () -> encryptionService.decrypt(null)
            );

            assertTrue(exception.getMessage().contains("nulo ou vazio"));
        }

        @Test
        @DisplayName("Deve lançar exceção ao descriptografar string vazia")
        void shouldThrowExceptionWhenDecryptingEmpty() {
            EncryptionException exception = assertThrows(
                EncryptionException.class,
                () -> encryptionService.decrypt("")
            );

            assertTrue(exception.getMessage().contains("nulo ou vazio"));
        }

        @Test
        @DisplayName("Deve lançar exceção ao descriptografar Base64 inválido")
        void shouldThrowExceptionWhenDecryptingInvalidBase64() {
            EncryptionException exception = assertThrows(
                EncryptionException.class,
                () -> encryptionService.decrypt("not-valid-base64!!!")
            );

            assertTrue(exception.getMessage().contains("Base64 malformado"));
        }

        @Test
        @DisplayName("Deve lançar exceção ao descriptografar com chave errada")
        void shouldThrowExceptionWhenDecryptingWithWrongKey() {
            String plaintext = "4532015112830366";
            String encrypted = encryptionService.encrypt(plaintext);

            // Criar novo service com chave diferente
            String differentKey = AesEncryptionService.generateNewKey();
            AesEncryptionService differentService = new AesEncryptionService(differentKey);

            EncryptionException exception = assertThrows(
                EncryptionException.class,
                () -> differentService.decrypt(encrypted)
            );

            assertTrue(exception.getMessage().contains("autenticação") ||
                      exception.getMessage().contains("chave incorreta"));
        }

        @Test
        @DisplayName("Deve lançar exceção ao descriptografar dados corrompidos")
        void shouldThrowExceptionWhenDecryptingCorruptedData() {
            String plaintext = "4532015112830366";
            String encrypted = encryptionService.encrypt(plaintext);

            // Corromper dados alterando um byte
            byte[] encryptedBytes = Base64.getDecoder().decode(encrypted);
            encryptedBytes[15] ^= 0xFF; // Flip bits
            String corrupted = Base64.getEncoder().encodeToString(encryptedBytes);

            EncryptionException exception = assertThrows(
                EncryptionException.class,
                () -> encryptionService.decrypt(corrupted)
            );

            assertTrue(exception.getMessage().contains("autenticação") ||
                      exception.getMessage().contains("corrompidos"));
        }

        @Test
        @DisplayName("Deve lançar exceção ao descriptografar dados truncados")
        void shouldThrowExceptionWhenDecryptingTruncatedData() {
            String plaintext = "4532015112830366";
            String encrypted = encryptionService.encrypt(plaintext);

            // Truncar dados
            String truncated = encrypted.substring(0, encrypted.length() - 10);

            assertThrows(
                EncryptionException.class,
                () -> encryptionService.decrypt(truncated)
            );
        }
    }

    @Nested
    @DisplayName("Round-trip (Criptografia + Descriptografia)")
    class RoundTrip {

        @Test
        @DisplayName("Deve preservar dados após round-trip")
        void shouldPreserveDataAfterRoundTrip() {
            String[] testCases = {
                "4532015112830366",
                "5425233430109903",
                "378282246310005",
                "6011111111111117",
                "1234567890123456"
            };

            for (String plaintext : testCases) {
                String encrypted = encryptionService.encrypt(plaintext);
                String decrypted = encryptionService.decrypt(encrypted);

                assertEquals(plaintext, decrypted,
                    "Round-trip deve preservar: " + plaintext);
            }
        }

        @Test
        @DisplayName("Deve preservar espaços em branco")
        void shouldPreserveWhitespace() {
            String plaintext = "  4532  0151  1283  0366  ";

            String encrypted = encryptionService.encrypt(plaintext);
            String decrypted = encryptionService.decrypt(encrypted);

            assertEquals(plaintext, decrypted);
        }

        @Test
        @DisplayName("Deve preservar quebras de linha")
        void shouldPreserveLineBreaks() {
            String plaintext = "Linha 1\nLinha 2\r\nLinha 3";

            String encrypted = encryptionService.encrypt(plaintext);
            String decrypted = encryptionService.decrypt(encrypted);

            assertEquals(plaintext, decrypted);
        }
    }

    @Nested
    @DisplayName("Geração de Chave")
    class KeyGeneration {

        @Test
        @DisplayName("Deve gerar chave válida")
        void shouldGenerateValidKey() {
            String key = AesEncryptionService.generateNewKey();

            assertNotNull(key);
            assertFalse(key.isEmpty());

            // Verificar que é Base64 válido
            byte[] keyBytes = Base64.getDecoder().decode(key);

            // Verificar tamanho (256 bits = 32 bytes)
            assertEquals(32, keyBytes.length);
        }

        @Test
        @DisplayName("Deve gerar chaves diferentes a cada chamada")
        void shouldGenerateDifferentKeys() {
            String key1 = AesEncryptionService.generateNewKey();
            String key2 = AesEncryptionService.generateNewKey();
            String key3 = AesEncryptionService.generateNewKey();

            assertNotEquals(key1, key2);
            assertNotEquals(key2, key3);
            assertNotEquals(key1, key3);
        }

        @Test
        @DisplayName("Chave gerada deve ser utilizável")
        void generatedKeyShouldBeUsable() {
            String key = AesEncryptionService.generateNewKey();

            assertDoesNotThrow(() -> {
                AesEncryptionService service = new AesEncryptionService(key);
                String encrypted = service.encrypt("teste");
                service.decrypt(encrypted);
            });
        }
    }

    @Nested
    @DisplayName("Segurança")
    class Security {

        @Test
        @DisplayName("Ciphertext não deve conter plaintext")
        void ciphertextShouldNotContainPlaintext() {
            String plaintext = "4532015112830366";

            String encrypted = encryptionService.encrypt(plaintext);

            assertFalse(encrypted.contains(plaintext),
                "Ciphertext não deve conter plaintext visível");
            assertFalse(encrypted.contains("4532"),
                "Ciphertext não deve conter partes do plaintext");
        }

        @Test
        @DisplayName("IVs devem ser únicos")
        void ivsShouldBeUnique() {
            String plaintext = "4532015112830366";

            // Gerar múltiplos ciphertexts
            String[] encrypted = new String[100];
            for (int i = 0; i < encrypted.length; i++) {
                encrypted[i] = encryptionService.encrypt(plaintext);
            }

            // Verificar que todos são diferentes (IVs diferentes)
            for (int i = 0; i < encrypted.length; i++) {
                for (int j = i + 1; j < encrypted.length; j++) {
                    assertNotEquals(encrypted[i], encrypted[j],
                        "Ciphertexts devem ser diferentes devido a IVs únicos");
                }
            }
        }

        @Test
        @DisplayName("Tamanho do ciphertext deve ser consistente para mesmo plaintext")
        void ciphertextSizeShouldBeConsistentForSamePlaintext() {
            String plaintext = "4532015112830366";

            String encrypted1 = encryptionService.encrypt(plaintext);
            String encrypted2 = encryptionService.encrypt(plaintext);

            assertEquals(encrypted1.length(), encrypted2.length(),
                "Ciphertexts de mesmo plaintext devem ter mesmo tamanho");
        }

        @Test
        @DisplayName("Tamanho do ciphertext deve crescer com plaintext")
        void ciphertextSizeShouldGrowWithPlaintext() {
            String short1 = encryptionService.encrypt("12");
            String medium = encryptionService.encrypt("1234567890123456");
            String long1 = encryptionService.encrypt("A".repeat(100));

            assertTrue(medium.length() > short1.length());
            assertTrue(long1.length() > medium.length());
        }
    }

    @Nested
    @DisplayName("Casos Especiais")
    class EdgeCases {

        @Test
        @DisplayName("Deve lidar com texto de um caractere")
        void shouldHandleSingleCharacter() {
            String plaintext = "A";

            String encrypted = encryptionService.encrypt(plaintext);
            String decrypted = encryptionService.decrypt(encrypted);

            assertEquals(plaintext, decrypted);
        }

        @Test
        @DisplayName("Deve lidar com texto muito longo")
        void shouldHandleVeryLongText() {
            String plaintext = "A".repeat(10000);

            String encrypted = encryptionService.encrypt(plaintext);
            String decrypted = encryptionService.decrypt(encrypted);

            assertEquals(plaintext, decrypted);
        }

        @Test
        @DisplayName("Deve lidar com todos os bytes possíveis")
        void shouldHandleAllPossibleBytes() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 256; i++) {
                sb.append((char) i);
            }
            String plaintext = sb.toString();

            String encrypted = encryptionService.encrypt(plaintext);
            String decrypted = encryptionService.decrypt(encrypted);

            assertEquals(plaintext, decrypted);
        }

        @Test
        @DisplayName("Deve lidar com string contendo apenas espaços")
        void shouldHandleOnlySpaces() {
            String plaintext = "    ";

            String encrypted = encryptionService.encrypt(plaintext);
            String decrypted = encryptionService.decrypt(encrypted);

            assertEquals(plaintext, decrypted);
        }
    }
}
