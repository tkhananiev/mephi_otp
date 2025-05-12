package otp.sf.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import otp.sf.domain.dto.ChangePasswordRequest;
import otp.sf.domain.dto.JwtAuthenticationResponse;
import otp.sf.domain.dto.SignInRequest;
import otp.sf.domain.dto.SignUpRequest;
import otp.sf.exception.LogicException;

/**
 * Сервис аутентификации
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    /**
     * Регистрация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public JwtAuthenticationResponse signUp(SignUpRequest request) {
        final var user = userService.addUser(request, passwordEncoder.encode(request.password()));
        final var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }

    /**
     * Аутентификация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public JwtAuthenticationResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.username(),
                request.password()
        ));

        final var user = userService
                .userDetailsService()
                .loadUserByUsername(request.username());

        final var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }

    /**
     * Смена пароля пользователя
     *
     * @param request новый пароль
     * @return токен
     */
    public JwtAuthenticationResponse passwordChange(ChangePasswordRequest request) {
        final var user = userService.getCurrentUser();
        final var newPassword = passwordEncoder.encode(request.password());
        if (newPassword.equals(user.getPassword())) {
            throw new LogicException("Новый пароль совпадает со старым");
        }

        user.setPassword(newPassword);
        userService.saveUser(user);

        final var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }
}
