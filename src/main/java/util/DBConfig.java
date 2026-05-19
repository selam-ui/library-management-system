package util;

import java.io.InputStream;
import java.util.Properties;

public class DBConfig {
    private static final Properties props = new Properties();
    static {
        try (InputStream in = DBConfig.class.getResourceAsStream("/db.properties")) {
            if (in == null) throw new RuntimeException("db.properties not found in classpath");
            props.load(in);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load db.properties", e);
        }
    }
    public static String get(String key) { return props.getProperty(key); }
}
