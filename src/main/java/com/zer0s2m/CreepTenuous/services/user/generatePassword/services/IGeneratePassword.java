package com.zer0s2m.CreepTenuous.services.user.generatePassword.services;

public interface IGeneratePassword {
    String generation(String password);
    Boolean verify(String password, String hashPassword);
}
