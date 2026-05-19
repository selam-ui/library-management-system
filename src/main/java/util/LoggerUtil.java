package util;

import java.util.logging.Logger;

public class LoggerUtil {
    public static Logger get(Class<?> cls) { return Logger.getLogger(cls.getName()); }
}
