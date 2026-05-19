package util;

public class Validator {
    public static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    public static boolean isEmail(String s) { return s != null && s.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"); }
    public static boolean isPositive(int n) { return n > 0; }
}
