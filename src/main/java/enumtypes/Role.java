package enumtypes;

public enum Role {
    ADMIN,
    LIBRARIAN,
    STUDENT;

    public static Role from(String value) {
        if (value == null || value.isBlank()) {
            return STUDENT;
        }
        switch (value.trim().toUpperCase()) {
            case "ADMIN":
                return ADMIN;
            case "LIBRARIAN":
                return LIBRARIAN;
            case "STUDENT":
            case "MEMBER":
                return STUDENT;
            default:
                return STUDENT;
        }
    }
}
