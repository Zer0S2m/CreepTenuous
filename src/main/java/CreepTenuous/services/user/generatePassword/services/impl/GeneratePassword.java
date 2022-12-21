package CreepTenuous.services.user.generatePassword.services.impl;

import CreepTenuous.services.user.generatePassword.services.IGeneratePassword;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.stereotype.Service;

@Service("generate-password")
public class GeneratePassword implements IGeneratePassword {
    private final Argon2 generate;

    public GeneratePassword() {
        this.generate = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 32, 64);
    }

    @Override
    public final String generation(String password) {
        return this.generate.hash(2, 15*1024, 1, password.toCharArray());
    }

    @Override
    public final Boolean verify(String password, String hashPassword) {
        return this.generate.verify(hashPassword, password.toCharArray());
    }
}
