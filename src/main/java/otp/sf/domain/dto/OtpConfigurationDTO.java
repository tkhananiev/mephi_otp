package otp.sf.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "OTP конфигурация: DTO запроса на изменение конфигурации")
public record OtpConfigurationDTO(
        @Schema(description = "Время жизни OTP-кодов в миллисекундах", example = "6000000")
        @Min(value = 10000, message = "Время жизни кода не может быть меньше 10-ти секунд")
        @Max(value = 144000000, message = "Время жизни кода не может быть больше суток")
        @NotNull(message = "Поле не может быть пустыми")
        long expirationTime,

        @Schema(description = "Размер OTP-кодов", example = "5")
        @Min(value = 5, message = "Длинна кода не может быть меньше 5-ти")
        @Max(value = 20, message = "Длинна кода не может быть больше 20-ти")
        @NotNull(message = "Поле не может быть пустыми")
        int length) {
}
