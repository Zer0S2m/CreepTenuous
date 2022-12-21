package CreepTenuous.services.user.generatePassword.services;

public interface IGeneratePassword {
    String generation(String password);
    Boolean verify(String password, String hashPassword);
}
