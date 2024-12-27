package utils;

import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtils {
    public static String getUserIdfromContext() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
