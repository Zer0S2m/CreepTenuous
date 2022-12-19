package CreepTenuous.services.User.GeneratePassword.services;

public interface IGeneratePassword {
    String generation(String password);
    Boolean verify(String password, String hashPassword);
}
