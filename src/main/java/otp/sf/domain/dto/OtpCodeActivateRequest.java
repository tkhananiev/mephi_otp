package otp.sf.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "OTP код: DTO запроса активацию кода")
public record OtpCodeActivateRequest(
        @Schema(description = "OTP код", example = "1234567890")
        @Size(min = 5, max = 20, message = "OTP код должен содержать от 5 до 20 символов")
        @NotBlank(message = "Поле не может быть пустым")
        String code) {
}
