package com.zer0s2m.creeptenuous.integration.implants.services.impl;

import org.apache.tomcat.util.codec.binary.Base64;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * Component for storing private and public keys
 */
class RSAKeys {

    static final public String TITLE_PRIVATE_KEY = "integration-main-system.pem";

    private final ResourceLoader resourceLoader = new DefaultResourceLoader();

    private final PrivateKey privateKey;

    public RSAKeys() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        this.privateKey = collectPrivateKey();
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    private PrivateKey collectPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String key = Files.readString(
                Path.of(resourceLoader.getResource("keys/" + TITLE_PRIVATE_KEY).getURI()),
                Charset.defaultCharset());
        key = cleanKeyFromFile(key);
        byte[] encoded = Base64.decodeBase64(key);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * clear key from file
     * @param rawKey raw key from file
     * @return clean key
     */
    private @NotNull String cleanKeyFromFile(@NotNull String rawKey) {
        return rawKey
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PRIVATE KEY-----", "");
    }

}
