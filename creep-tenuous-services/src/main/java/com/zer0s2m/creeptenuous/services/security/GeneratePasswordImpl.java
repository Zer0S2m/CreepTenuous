package com.zer0s2m.creeptenuous.services.security;

import com.zer0s2m.creeptenuous.security.services.GeneratePassword;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.stereotype.Service;

/**
 * Service for creating and verifying passwords for users in the system
 */
@Service("generate-password")
public class GeneratePasswordImpl implements GeneratePassword {

    private final Argon2 generate;

    public GeneratePasswordImpl() {
        this.generate = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 32, 64);
    }

    /**
     * Generate password
     * @param password raw password
     * @return hash password
     */
    @Override
    public final String generation(String password) {
        return this.generate.hash(2, 15 * 1024, 1, password.toCharArray());
    }

    /**
     * Verify password
     * @param password raw password
     * @param hashPassword hash password
     * @return is valid
     */
    @Override
    public final Boolean verify(String password, String hashPassword) {
        return this.generate.verify(hashPassword, password.toCharArray());
    }

}
