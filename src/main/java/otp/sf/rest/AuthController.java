package otp.sf.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import otp.sf.domain.dto.ChangePasswordRequest;
import otp.sf.domain.dto.JwtAuthenticationResponse;
import otp.sf.domain.dto.SignInRequest;
import otp.sf.domain.dto.SignUpRequest;
import otp.sf.service.AuthenticationService;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "REST API: Аутентификация")
public class AuthController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "Регистрация пользователя")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/sign/up",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public JwtAuthenticationResponse signUp(@RequestBody @Valid SignUpRequest request) {
        log.info("Регистрация пользователя {}", request.username());
        return authenticationService.signUp(request);
    }

    @Operation(summary = "Авторизация пользователя")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/sign/in",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public JwtAuthenticationResponse signIn(@RequestBody @Valid SignInRequest request) {
        log.info("Авторизация пользователя {}", request.username());
        return authenticationService.signIn(request);
    }

    @Operation(summary = "Смена пароля пользователя")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/password/change",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public JwtAuthenticationResponse passwordChange(@RequestBody @Valid ChangePasswordRequest request) {
        log.info("Смена пароля пользователя");
        return authenticationService.passwordChange(request);
    }
}
