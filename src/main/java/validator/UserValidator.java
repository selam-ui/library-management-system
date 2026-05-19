package validator;

import exception.ValidationException;
import model.User;
import util.Validator;

public class UserValidator {
    public static void validateLogin(String username, String password) throws ValidationException {
        if (Validator.isBlank(username)) throw new ValidationException("Username is required");
        if (Validator.isBlank(password)) throw new ValidationException("Password is required");
    }

    public static void validate(User u, boolean isNew) throws ValidationException {
        if (u == null) throw new ValidationException("User is null");
        if (Validator.isBlank(u.getUsername())) throw new ValidationException("Username required");
        if (isNew && Validator.isBlank(u.getPassword())) throw new ValidationException("Password required");
        if (u.getEmail() != null && !u.getEmail().isEmpty() && !Validator.isEmail(u.getEmail()))
            throw new ValidationException("Invalid email");
    }
}
