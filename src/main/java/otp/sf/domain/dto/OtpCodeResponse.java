package otp.sf.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import otp.sf.domain.model.Status;

@Schema(description = "OTP код: DTO ответа")
public record OtpCodeResponse(
        @Schema(description = "Идентификатор операции", example = "1")
        long id,

        @Schema(description = "Статус OTP кода", example = "Активен")
        Status status) {
}
