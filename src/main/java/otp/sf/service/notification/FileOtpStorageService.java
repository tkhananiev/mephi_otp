package otp.sf.service.notification;

import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class FileOtpStorageService {
    private static final String FILE_NAME = "otp_codes.txt";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void saveOtpCode(String username, String code) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String line = String.format("[%s] User: %s | OTP: %s%n", timestamp, username, code);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Paths.get(FILE_NAME).toFile(), true))) {
            writer.write(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
