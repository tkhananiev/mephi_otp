package otp.sf.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на смену пароля")
public record ChangePasswordRequest(
        @Schema(description = "Пароль", example = "my_1secret1_password")
        @Size(min = 8, max = 255, message = "Длина пароля должна быть от 8 до 255 символов")
        @NotBlank(message = "Пароль не может быть пустыми")
        String password) {
}
