package service;

import exception.AuthenticationException;
import exception.ValidationException;
import model.User;

public interface AuthService {
    User login(String username, String password) throws AuthenticationException, ValidationException;
    void logout();
}
