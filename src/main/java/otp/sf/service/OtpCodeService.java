package otp.sf.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import otp.sf.domain.dto.OtpCodeActivateRequest;
import otp.sf.domain.dto.OtpCodeCreateRequest;
import otp.sf.domain.dto.OtpCodeResponse;
import otp.sf.domain.model.OtpCode;
import otp.sf.domain.model.Role;
import otp.sf.domain.model.Status;
import otp.sf.exception.LogicException;
import otp.sf.repository.OtpCodeRepository;
import otp.sf.service.notification.FileOtpStorageService;
import otp.sf.service.notification.NotificationService;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

/**
 * Сервис управления категориями
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class OtpCodeService {

    private static final String ALPHABET = "1234567890";
    private final Random random = new Random();

    private final OtpConfigurationService configurationService;
    private final OtpCodeRepository repository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final List<NotificationService> notificationServices;

    /**
     * Добавление OTP-кода
     *
     * @param request данные OTP-кода
     * @return информация о OTP-коде
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public OtpCodeResponse createCode(final OtpCodeCreateRequest request) {
        var user = userService.getCurrentUser();
        if (repository.existsByOperationIdAndStatusAndUser(request.operationId(), Status.ACTIVE, user)) {
            throw new LogicException("Найден активный OTP-код для операции");
        }

        var config = configurationService.getConfiguration();

        var newKey = generateKey(config.length(), value ->
                repository.existsByCodeAndStatusAndUser(value, Status.ACTIVE, user));

        var code = new OtpCode();
        code.setUser(user);
        code.setStatus(Status.ACTIVE);
        code.setOperationId(request.operationId());
        code.setCode(passwordEncoder.encode(newKey));
        code.setExpirationTime(config.expirationTime());
        repository.save(code);
        try {
            fileOtpStorageService.saveOtpCode(user.toString(),code.toString());
        } catch (Exception e){
            log.error("Не удалось сохранить код в файл",e);
        }

        var sendResult = false;
        for(var service : notificationServices) {
            sendResult |= service.sendOtpCode(user, newKey);
        }

        if (!sendResult) {
            throw new LogicException("Не получилось отправить OTP-код пользователю");
        }

        return convertToResponse(code);
    }

    /**
     * Удаление OTP-кода
     *
     * @param id идентификатор OTP-кода
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteCode(final Long id) {
        final var code = repository.findById(id)
                .orElseThrow(() -> new LogicException("OTP-код не найден"));
        repository.delete(code);
    }

    /**
     * Активация OTP-кода
     *
     * @param id идентификатор операции
     * @param request данные OTP-кода
     * @return информация о OTP-коде
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public OtpCodeResponse activateCode(final Long id, final OtpCodeActivateRequest request) {
        var user = userService.getCurrentUser();
        if (!repository.existsByOperationIdAndStatusAndUser(id, Status.ACTIVE, user)) {
            throw new LogicException("Активный OTP-код для операции не найден");
        }

        var code = repository.findByOperationIdAndStatusAndUser(id, Status.ACTIVE, user)
                .orElseThrow(IllegalStateException::new);
        final var inputKey = passwordEncoder.encode(request.code());
        if (inputKey.equals(code.getCode())) {
            throw new LogicException("OTP-код не подходит");
        }
        code.setStatus(Status.USED);
        repository.save(code);

        return convertToResponse(code);
    }

    /**
     * Информация о OTP-коде пользователя
     *
     * @return информация о OTP-коде
     */
    public OtpCodeResponse getCodeInfo(Long id) {
        var user = userService.getCurrentUser();
        if (Role.ROLE_ADMIN.equals(user.getRole())) {
            return repository.findByOperationId(id)
                    .map(this::convertToResponse)
                    .orElseThrow(() -> new LogicException("OTP-код не найден"));
        } else {
            return repository.findByOperationIdAndUser(id, user)
                    .map(this::convertToResponse)
                    .orElseThrow(() -> new LogicException("OTP-код не найден"));
        }
    }

    /**
     * Выборка всех активных OTP-кодов
     *
     * @return список активных OTP-кодов
     */
    public List<OtpCodeResponse> getAllCodeInfo() {
        var user = userService.getCurrentUser();
        if (Role.ROLE_ADMIN.equals(user.getRole())) {
            return repository.findByStatus(Status.ACTIVE).stream()
                    .map(this::convertToResponse)
                    .toList();
        } else {
            return repository.findByStatusAndUser(Status.ACTIVE, user).stream()
                    .map(this::convertToResponse)
                    .toList();
        }
    }

    private OtpCodeResponse convertToResponse(OtpCode input) {
        return new OtpCodeResponse(input.getId(), input.getStatus());
    }

    /**
     * Метод формирования уникального кода ссылки для короткой ссылки
     * Каждый символ кода выбирается случайным образом, после чего код проверяется на совпадение с уже
     * сохраненными активными кодами, если совпадений нет, то код принимается, если нет - пробуем еще раз
     *
     * @param size размер кода
     * @param existChecker лямбда для проверки уникальности кода
     *
     * @return уникальный для пользователя код
     */
    private String generateKey(final int size, final Predicate<String> existChecker) {
        String key;
        do {
            // Генерация строки случайным образом
            var sb = new StringBuilder();
            for (int i = 0; i < size; i++) {
                sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
            }
            key = sb.toString();
        } while (existChecker.test(key));

        return key;
    }

    /**
     * Проверка по крону истекших по времени кодов OTP
     * метод вызывается раз в минуту
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Scheduled(cron = "0 * * * * *")
    @Async
    public void checkExpiringCode() {
        final var activeCodeList = repository.findByStatus(Status.ACTIVE);
        final long currentMilliseconds = System.currentTimeMillis();
        for (final var activeCode : activeCodeList) {
            if (currentMilliseconds - activeCode.getInsertTime().getTime() > activeCode.getExpirationTime()) {
                activeCode.setStatus(Status.EXPIRED);
                log.info("OTP-код {} просрочен", activeCode.getCode());
            }
        }
        repository.saveAll(activeCodeList);
    }

    @Autowired
    private FileOtpStorageService fileOtpStorageService;
}
