package otp.sf.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import otp.sf.domain.dto.SignUpRequest;
import otp.sf.domain.dto.UserDTO;
import otp.sf.domain.model.Role;
import otp.sf.domain.model.User;
import otp.sf.exception.LogicException;
import otp.sf.repository.UserRepository;

import java.util.List;

/**
 * Сервис управления пользователями
 */
@RequiredArgsConstructor
@Service
@Transactional
public class UserService {

    private final UserRepository repository;

    /**
     * Создание пользователя
     *
     * @param request данные пользователя
     * @return созданный пользователь
     */
    public User addUser(final SignUpRequest request, final String encodedPassword) {
        if (repository.existsByUsername(request.username())) {
            throw new LogicException("Пользователь с таким именем уже существует");
        }

        final var user = User.builder()
                .username(request.username())
                .password(encodedPassword)
                .role(Role.ROLE_USER)
                .email(request.email())
                .phone(request.phone())
                .telegramChatId(request.telegramId())
                .build();

        return repository.save(user);
    }

    /**
     * Обновление пользователя
     */
    public void saveUser(final User user) {
        repository.save(user);
    }

    /**
     * Удаление пользователя
     *
     * @param id идентификатор пользователя
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteUser(final Long id) {
        final var user = getCurrentUser();
        if (user.getId().equals(id)) {
            throw new LogicException("Нельзя удалить себя");
        }
        repository.deleteById(id);
    }

    /**
     * Обновление пользователя
     *
     * @return пользователь
     */
    public UserDTO saveUser(final UserDTO request) {
        final var user = getCurrentUser();
        if (repository.existsByUsername(request.username())) {
            throw new LogicException("Пользователь с таким именем уже существует");
        }

        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setTelegramChatId(request.telegramChatId());
        repository.save(user);

        return convertToResponse(user);
    }

    /**
     * Получение пользователя по имени пользователя
     *
     * @return пользователь
     */
    public User getByUsername(final String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new LogicException("Пользователь не найден"));

    }

    /**
     * Получение пользователя по имени пользователя
     * Нужен для Spring Security
     *
     * @return пользователь
     */
    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    /**
     * Получение текущего пользователя
     *
     * @return текущий пользователь
     */
    public User getCurrentUser() {
        // Получение имени пользователя из контекста Spring Security
        final var username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return getByUsername(username);
    }

    /**
     * Выборка всех пользователей
     *
     * @return список пользователей
     */
    public List<UserDTO> getAllUsers() {
        return repository.findByRoleNot(Role.ROLE_ADMIN).stream()
                .map(this::convertToResponse)
                .toList();
    }

    private UserDTO convertToResponse(User user) {
        return new UserDTO(user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getTelegramChatId());
    }

    /**
     * Выдача прав администратора пользователю
     *
     * @param id идентификатор пользователя
     */
    public void setAdmin(final Long id) {
        final var user = repository.findById(id)
                .orElseThrow(() -> new LogicException("Пользователь не найден"));
        user.setRole(Role.ROLE_ADMIN);
        repository.save(user);
    }
}
