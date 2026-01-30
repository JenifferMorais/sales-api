package com.sales.infrastructure.security;

import com.sales.domain.shared.exception.EncryptionException;
import com.sales.domain.shared.port.EncryptionService;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Implementação de criptografia usando AES-256-GCM.
 *
 * Formato da saída: Base64(IV || ciphertext || auth_tag)
 * - IV: 12 bytes (gerado aleatoriamente por operação)
 * - Tag de autenticação: 128 bits
 * - Algoritmo: AES/GCM/NoPadding
 */
@ApplicationScoped
public class AesEncryptionService implements EncryptionService {

    private static final Logger LOG = Logger.getLogger(AesEncryptionService.class);

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12; // 96 bits
    private static final int GCM_TAG_LENGTH = 128; // 128 bits
    private static final int AES_KEY_SIZE = 256; // 256 bits

    private final SecretKey secretKey;
    private final SecureRandom secureRandom;

    public AesEncryptionService(
            @ConfigProperty(name = "encryption.key.base64") String base64Key) {

        if (base64Key == null || base64Key.isBlank()) {
            throw new EncryptionException(
                "Chave de criptografia não configurada. " +
                "Configure a variável de ambiente ENCRYPTION_KEY_BASE64");
        }

        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64Key);

            if (keyBytes.length != 32) { // 256 bits = 32 bytes
                throw new EncryptionException(
                    "Chave de criptografia deve ter 256 bits (32 bytes). " +
                    "Tamanho atual: " + (keyBytes.length * 8) + " bits");
            }

            this.secretKey = new SecretKeySpec(keyBytes, "AES");
            this.secureRandom = new SecureRandom();

            LOG.info("AesEncryptionService inicializado com chave AES-256");

        } catch (IllegalArgumentException e) {
            throw new EncryptionException(
                "Chave de criptografia inválida (Base64 malformado)", e);
        }
    }

    @Override
    public String encrypt(String plaintext) {
        if (plaintext == null || plaintext.isEmpty()) {
            throw new EncryptionException("Texto para criptografar não pode ser nulo ou vazio");
        }

        try {
            // Gerar IV aleatório
            byte[] iv = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);

            // Configurar cipher
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            // Criptografar
            byte[] plaintextBytes = plaintext.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            byte[] ciphertext = cipher.doFinal(plaintextBytes);

            // Combinar IV + ciphertext (que já inclui auth tag)
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + ciphertext.length);
            byteBuffer.put(iv);
            byteBuffer.put(ciphertext);

            // Codificar em Base64
            String encrypted = Base64.getEncoder().encodeToString(byteBuffer.array());

            LOG.debugf("Texto criptografado: %d bytes plaintext -> %d chars Base64",
                      plaintextBytes.length, encrypted.length());

            return encrypted;

        } catch (Exception e) {
            throw new EncryptionException("Erro ao criptografar dados", e);
        }
    }

    @Override
    public String decrypt(String ciphertext) {
        if (ciphertext == null || ciphertext.isEmpty()) {
            throw new EncryptionException("Texto para descriptografar não pode ser nulo ou vazio");
        }

        try {
            // Decodificar Base64
            byte[] decoded = Base64.getDecoder().decode(ciphertext);

            // Extrair IV e ciphertext
            ByteBuffer byteBuffer = ByteBuffer.wrap(decoded);
            byte[] iv = new byte[GCM_IV_LENGTH];
            byteBuffer.get(iv);

            byte[] ciphertextBytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(ciphertextBytes);

            // Configurar cipher
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            // Descriptografar e verificar autenticidade
            byte[] plaintextBytes = cipher.doFinal(ciphertextBytes);
            String plaintext = new String(plaintextBytes, java.nio.charset.StandardCharsets.UTF_8);

            LOG.debugf("Texto descriptografado: %d chars Base64 -> %d bytes plaintext",
                      ciphertext.length(), plaintextBytes.length);

            return plaintext;

        } catch (IllegalArgumentException e) {
            throw new EncryptionException("Dados criptografados inválidos (Base64 malformado)", e);
        } catch (javax.crypto.AEADBadTagException e) {
            throw new EncryptionException(
                "Falha na autenticação dos dados. " +
                "Dados podem ter sido corrompidos ou chave incorreta", e);
        } catch (Exception e) {
            throw new EncryptionException("Erro ao descriptografar dados", e);
        }
    }

    /**
     * Gera uma nova chave AES-256 aleatória.
     * Útil para configuração inicial de ambientes.
     *
     * @return Chave codificada em Base64
     */
    public static String generateNewKey() {
        byte[] key = new byte[32]; // 256 bits
        new SecureRandom().nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }
}
