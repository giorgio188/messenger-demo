package com.project.messenger.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import java.security.SecureRandom;

@Configuration
public class EncryptionConfig {

    @Bean
    public TextEncryptor textEncryptor() {
        Dotenv dotenv = Dotenv.configure().load();
        String password = dotenv.get("ENCRYPTION_PASSWORD");
        String salt = dotenv.get("ENCRYPTION_SALT");
        System.out.println("Encryption password: " + password);
        System.out.println("Encryption salt: " + salt);
        return Encryptors.text(password, salt);
    }
}
