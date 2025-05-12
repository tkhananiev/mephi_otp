package otp.sf.service.notification;

import otp.sf.domain.model.User;

/**
 * Уведомление пользователя
 */
public interface NotificationService {

    /**
     * Отправка OTP кода
     *
     * @param user пользователь
     * @param otpCode OTP код
     */
    boolean sendOtpCode(User user, String otpCode);
}
