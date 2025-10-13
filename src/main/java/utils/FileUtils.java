package utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class FileUtils {

    private static final Map<String, Sort> SORTING_MAP = new HashMap<>();

    static {
        SORTING_MAP.put("name_ascending", Sort.by("fileName").ascending()); // Specification needs the class field name not the field name from the database table
        SORTING_MAP.put("name_descending", Sort.by("fileName").descending());
        SORTING_MAP.put("date_ascending", Sort.by("timestamp").ascending());
        SORTING_MAP.put("date_descending", Sort.by("timestamp").descending());
        SORTING_MAP.put("size_ascending", Sort.by("sizeBytes").ascending());
        SORTING_MAP.put("size_descending", Sort.by("sizeBytes").descending());
    }

    public static boolean stringIsNullorBlank(String string) {
        return string == null || string.isBlank();
    }


    public static Sort findSorting(String sorting) {
        return SORTING_MAP.getOrDefault(sorting, Sort.by("file_name").ascending());
    }

    public static String bytesToHumanReadable(long bytes) {
        if (bytes < 1000) {
            return bytes + " B";
        }
        int unit = 1000;
        String[] units = {"KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};

        // Calculate the index of the unit
        int index = (int) (Math.log(bytes) / Math.log(unit));
        // Round the bytes to two decimal places
        double value = bytes / Math.pow(unit, index);

        return String.format("%.2f %s", value, units[index - 1]);
    }

    public static String truncateString(String input, int maxLength) {
        if (input != null && input.length() > maxLength) return input.substring(0, maxLength);
        return input;
    }

}