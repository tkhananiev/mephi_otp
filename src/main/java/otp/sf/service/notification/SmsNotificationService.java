package otp.sf.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.smpp.Session;
import org.smpp.TCPIPConnection;
import org.smpp.pdu.BindTransmitter;
import org.smpp.pdu.SubmitSM;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import otp.sf.domain.model.User;

/**
 * Сервис рассылки SMS-сообщений
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SmsNotificationService implements NotificationService {

    @Value("${notification.smpp.host}")
    private String host;
    @Value("${notification.smpp.port}")
    private int port;
    @Value("${notification.smpp.system_id}")
    private String systemId;
    @Value("${notification.smpp.password}")
    private String password;
    @Value("${notification.smpp.system_type}")
    private String systemType;
    @Value("${notification.smpp.source_addr}")
    private String sourceAddress;

    /**
     * {@inheritDoc}
     */
    public boolean sendOtpCode(final User user, final String otpCode) {
        try {
            // 1. Установка соединения
            final var connection = new TCPIPConnection(host, port);
            final var session = new Session(connection);
            // 2. Подготовка Bind Request
            final var bindRequest = new BindTransmitter();
            bindRequest.setSystemId(systemId);
            bindRequest.setPassword(password);
            bindRequest.setSystemType(systemType);
            bindRequest.setInterfaceVersion((byte) 0x34); // SMPP v3.4
            bindRequest.setAddressRange(sourceAddress);
            // 3. Выполнение привязки
            final var bindResponse = session.bind(bindRequest);
            if (bindResponse.getCommandStatus() != 0) {
                throw new Exception("Bind failed: " + bindResponse.getCommandStatus());
            }
            // 4. Отправка сообщения
            final var submitSM = new SubmitSM();
            submitSM.setSourceAddr(sourceAddress);
            submitSM.setDestAddr(user.getPhone());
            submitSM.setShortMessage(String.format("OTP: %s", otpCode));

            session.submit(submitSM);

            log.info("Сообщение Sms отправлено успешно");

            return true;
        } catch (Exception e) {
            log.error("Ошибка отправки Sms: {}", e.getMessage());
            return false;
        }
    }
}
