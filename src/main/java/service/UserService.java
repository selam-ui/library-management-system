package service;

import exception.ValidationException;
import model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> listAll();
    User create(User user) throws ValidationException;
    User register(User user) throws ValidationException;
    boolean update(User user) throws ValidationException;
    boolean delete(int id) throws ValidationException;
    boolean approve(int id) throws ValidationException;
    boolean activate(int id) throws ValidationException;
    boolean deactivate(int id) throws ValidationException;
    Optional<User> findById(int id);
}
