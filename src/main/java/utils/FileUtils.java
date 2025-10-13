package utils;

import com.example.FileShareAPI.Back_End.exception.InsufficientStorageException;
import com.example.FileShareAPI.Back_End.exception.UnAuthorizedException;
import com.example.FileShareAPI.Back_End.model.File;
import com.example.FileShareAPI.Back_End.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    public static void hasAccessToFile(File file) {
        if (!(file.getIsPublic() || Objects.equals(file.getUser().getUserId(), UserUtils.getUserIdfromContext()))) {
            throw new UnAuthorizedException("You don't have access to this file");
        }
    }

    public static void hasEnoughFreeSpace(User user, long fileSize) {
        long allowedSpace = user.getRole().getTotalAvailableBytes();
        long usedSpace = user.getTotalMemoryUsedBytes();
        if (usedSpace + fileSize > allowedSpace) {
            throw new InsufficientStorageException("Insufficient free space. You have " +
                    bytesToHumanReadable(allowedSpace - usedSpace) + " of free space left");
        }
    }

    public static boolean isFileOwner(File file) {
        String userId = UserUtils.getUserIdfromContext();
        return file.getUser().getUserId().equals(userId);
    }
}