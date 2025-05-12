package otp.sf.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на регистрацию")
public record SignUpRequest(
        @Schema(description = "Имя пользователя", example = "Вася")
        @Size(min = 1, max = 50, message = "Имя пользователя должно содержать от 1 до 50 символов")
        @NotBlank(message = "Имя пользователя не может быть пустыми")
        String username,

        @Schema(description = "Пароль", example = "my_1secret1_password")
        @Size(min = 8, max = 255, message = "Длина пароля должна быть от 8 до 255 символов")
        @NotBlank(message = "Пароль не может быть пустыми")
        String password,

        @Schema(description = "Email пользователя", example = "your_email@example.com")
        @Email(message = "Адрес Email должен быть валидным")
        @NotBlank(message = "Поле не может быть пустым")
        String email,

        @Schema(description = "Телефон пользователя")
        @Pattern(regexp = "^(8|\\+7)\\d{10}",
                message = "Номер телефона должен соответствовать формат (+7 или 8) + 10 символов")
        @NotBlank(message = "Телефон не может быть пустым")
        String phone,

        @Schema(description = "Идентификатор чата Telegram пользователя", example = "-123456789")
        @NotBlank(message = "Поле не может быть пустым")
        String telegramId) {
}
