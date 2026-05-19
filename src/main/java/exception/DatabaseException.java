package exception;
public class DatabaseException extends RuntimeException {
    public DatabaseException(String m, Throwable t) { super(m, t); }
    public DatabaseException(String m) { super(m); }
}
