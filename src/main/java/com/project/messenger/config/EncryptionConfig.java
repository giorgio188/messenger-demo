package com.project.messenger.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import java.security.SecureRandom;

@Configuration
public class EncryptionConfig {

    @Bean
    public TextEncryptor textEncryptor() {
        Dotenv dotenv = Dotenv.configure().load();
        String password = dotenv.get("ENCRYPTION_PASSWORD");
        String salt = generateSalt();
        return Encryptors.text(password, salt);
    }

    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        return new String(saltBytes);
    }
}
