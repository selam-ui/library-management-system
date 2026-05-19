package dao.interfaces;

import model.User;
import java.util.List;
import java.util.Optional;

public interface UserDAO {
    List<User> findAll();
    Optional<User> findById(int id);
    Optional<User> findByUsername(String username);
    User save(User user);
    boolean update(User user);
    boolean delete(int id);
}
