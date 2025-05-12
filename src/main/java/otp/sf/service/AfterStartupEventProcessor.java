package otp.sf.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import otp.sf.domain.dto.OtpConfigurationDTO;
import otp.sf.domain.model.Role;
import otp.sf.domain.model.User;
import otp.sf.exception.LogicException;

/**
 * Проверка и добавление данных в БД при старте сервиса
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class AfterStartupEventProcessor {

    private final OtpConfigurationService otpConfigurationService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    // Время жизни OTP-кода по умолчанию
    @Value("${default.configuration.expirationTime}")
    private long expirationTime;

    // Длинна OTP-кода по умолчанию
    @Value("${default.configuration.length}")
    private int length;

    // Логин первого администратора
    @Value("${default.user.admin.username}")
    private String adminUsername;

    // Пароль первого администратора
    @Value("${default.user.admin.password}")
    private String adminPassword;

    /**
     * Обработка события старта приложения
     */
    @EventListener(ApplicationReadyEvent.class)
    public void startup() {
        checkConfiguration();
        checkUserAdmin();
    }

    /**
     * Проверка наличия конфигурации в БД
     */
    private void checkConfiguration() {
        try {
            final var configuration = otpConfigurationService.getConfiguration();
        } catch (LogicException e) {
            log.info("Добавление конфигурации по умолчанию");
            otpConfigurationService.updateConfiguration(new OtpConfigurationDTO(expirationTime, length));
        }
    }

    /**
     * Проверка наличия первого администратора в БД
     */
    private void checkUserAdmin() {
        try {
            final var admin = userService.getByUsername(adminUsername);
        } catch (LogicException e) {
            log.info("Добавление первого администратора");
            final var user = User.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode(adminPassword))
                    .role(Role.ROLE_ADMIN)
                    .build();
            userService.saveUser(user);
        }
    }
}
