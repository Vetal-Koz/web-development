package web.dev.webdev.service;

import web.dev.webdev.dto.RegistrationDto;
import web.dev.webdev.models.UserEntity;

public interface UserService {
    void saveUser(RegistrationDto registrationDto);
    UserEntity findByEmail(String email);
    UserEntity findByUsername(String username);
}
