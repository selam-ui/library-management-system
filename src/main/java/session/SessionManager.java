package session;

import model.User;

public class SessionManager {
    private static User currentUser;
    public static void login(User u) { currentUser = u; }
    public static void logout() { currentUser = null; }
    public static User getCurrentUser() { return currentUser; }
    public static boolean isLoggedIn() { return currentUser != null; }
}
