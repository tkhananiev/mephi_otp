package otp.sf.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "OTP код: DTO запроса на создание кода")
public record OtpCodeCreateRequest(
        @Schema(description = "Идентификатор операции", example = "1")
        @NotNull(message = "Поле не может быть пустыми")
        long operationId) {
}
