package otp.sf.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import otp.sf.domain.model.User;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Сервис рассылки SMS-сообщений
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class TelegramNotificationService implements NotificationService {

    @Value("${notification.telegram.bot.token}")
    private String token;

    /**
     * {@inheritDoc}
     */
    public boolean sendOtpCode(final User user, final String otpCode) {
        try {
            final var url = String.format("https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s",
                    token,
                    user.getTelegramChatId(),
                    urlEncode("OTP: " + otpCode));

            return sendTelegramRequest(url);
        } catch (Exception e) {
            log.error("Ошибка отправки Telegram: {}", e.getMessage());
            return false;
        }
    }

    private boolean sendTelegramRequest(final String url) throws IOException {
        var result = false;
        try (var httpClient = HttpClients.createDefault()) {
            var request = new HttpGet(url);
            try (var response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    log.error("Telegram API error. Status code: {}", statusCode);
                } else {
                    log.info("Сообщение Telegram отправлено успешно");
                    result = true;
                }
            }
        }

        return result;
    }

    private static String urlEncode(final String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
