package service.impl;

import dao.impl.UserDAOImpl;
import dao.interfaces.UserDAO;
import enumtypes.Role;
import exception.ValidationException;
import model.User;
import service.UserService;
import session.SessionManager;
import util.PasswordUtil;
import util.Validator;
import validator.UserValidator;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {
    private final UserDAO dao = new UserDAOImpl();

    private void assertAdminAccess() throws ValidationException {
        var current = SessionManager.getCurrentUser();
        if (current == null) throw new ValidationException("You must be logged in to manage users.");
        if (current.getRole() != Role.ADMIN) throw new ValidationException("Only administrators can manage users.");
    }

    @Override
    public List<User> listAll() {
        return dao.findAll();
    }

    @Override
    public User create(User user) throws ValidationException {
        assertAdminAccess();
        UserValidator.validate(user, true);
        if (user.getRole() == null) user.setRole(Role.STUDENT);
        user.setApproved(true);
        user.setActive(true);
        user.setPassword(PasswordUtil.hash(user.getPassword()));
        return dao.save(user);
    }

    @Override
    public User register(User user) throws ValidationException {
        UserValidator.validate(user, true);
        user.setRole(Role.STUDENT);
        user.setApproved(false);
        user.setActive(false);
        user.setPassword(PasswordUtil.hash(user.getPassword()));
        return dao.save(user);
    }

    @Override
    public boolean update(User user) throws ValidationException {
        assertAdminAccess();
        if (user.getId() <= 0) throw new ValidationException("Select a user to update.");
        User existing = dao.findById(user.getId()).orElseThrow(() -> new ValidationException("User not found."));
        if (user.getRole() == null) user.setRole(existing.getRole());
        if (Validator.isBlank(user.getPassword())) {
            user.setPassword(existing.getPassword());
            UserValidator.validate(user, false);
        } else {
            UserValidator.validate(user, true);
            if (!user.getPassword().startsWith("$2a$") && !user.getPassword().startsWith("$2b$")) {
                user.setPassword(PasswordUtil.hash(user.getPassword()));
            }
        }
        user.setApproved(existing.isApproved());
        user.setActive(existing.isActive());
        return dao.update(user);
    }

    @Override
    public boolean delete(int id) throws ValidationException {
        assertAdminAccess();
        var current = SessionManager.getCurrentUser();
        if (current != null && current.getId() == id) {
            throw new ValidationException("You cannot delete your own account while logged in.");
        }
        return dao.delete(id);
    }

    @Override
    public boolean approve(int id) throws ValidationException {
        assertAdminAccess();
        User user = dao.findById(id).orElseThrow(() -> new ValidationException("User not found."));
        if (user.isApproved()) return false;
        user.setApproved(true);
        user.setActive(true);
        return dao.update(user);
    }

    @Override
    public boolean activate(int id) throws ValidationException {
        assertAdminAccess();
        User user = dao.findById(id).orElseThrow(() -> new ValidationException("User not found."));
        if (!user.isApproved()) throw new ValidationException("User must be approved before activation.");
        if (user.isActive()) return false;
        user.setActive(true);
        return dao.update(user);
    }

    @Override
    public boolean deactivate(int id) throws ValidationException {
        assertAdminAccess();
        User user = dao.findById(id).orElseThrow(() -> new ValidationException("User not found."));
        if (!user.isActive()) return false;
        user.setActive(false);
        return dao.update(user);
    }

    @Override
    public Optional<User> findById(int id) {
        return dao.findById(id);
    }
}
