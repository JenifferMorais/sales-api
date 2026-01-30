package com.sales.domain.shared.port;

/**
 * Port para criptografia de dados sensíveis.
 * Implementações devem usar AES-256-GCM ou superior.
 */
public interface EncryptionService {
    String encrypt(String plaintext);
    String decrypt(String ciphertext);
}
