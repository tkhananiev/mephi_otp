package otp.sf.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import otp.sf.domain.dto.OtpCodeActivateRequest;
import otp.sf.domain.dto.OtpCodeCreateRequest;
import otp.sf.domain.dto.OtpCodeResponse;
import otp.sf.domain.dto.OtpConfigurationDTO;
import otp.sf.service.OtpCodeService;
import otp.sf.service.OtpConfigurationService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("otp")
@Tag(name = "REST API: OTP-коды")
public class OtpCodeController {

    private final OtpConfigurationService otpConfigurationService;
    private final OtpCodeService otpCodeService;

    @Operation(summary = "Создание OTP-кода")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/create",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public OtpCodeResponse createCode(@RequestBody @Valid OtpCodeCreateRequest code) {
        log.info("Создание OTP-кода операции {}", code.operationId());
        return otpCodeService.createCode(code);
    }

    @Operation(summary = "Активация OTP-кода")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/{id}/activate",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public OtpCodeResponse useCode(@PathVariable("id") Long id, @RequestBody @Valid OtpCodeActivateRequest code) {
        log.info("Активация OTP-кода операции {}", id);
        return otpCodeService.activateCode(id, code);
    }

    @Operation(summary = "Информация OTP-кода")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}/get",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public OtpCodeResponse getCodeInfo(@PathVariable("id") Long id) {
        log.info("Информация OTP-кода операции {}", id);
        return otpCodeService.getCodeInfo(id);
    }

    @Operation(summary = "Список всех OTP-кодов")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/all",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OtpCodeResponse> getAllCodes() {
        log.info("Список всех OTP-кодов");
        return otpCodeService.getAllCodeInfo();
    }

    @Operation(summary = "Удаление OTP-кода")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCode(@PathVariable("id") Long id) {
        log.info("Удаление OTP-кода {}", id);
        otpCodeService.deleteCode(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Изменение конфигурации")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/configuration/update",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public OtpConfigurationDTO updateConfiguration(@RequestBody @Valid OtpConfigurationDTO configuration) {
        log.info("Изменение конфигурации");
        otpConfigurationService.updateConfiguration(configuration);
        return configuration;
    }

    @Operation(summary = "Получение конфигурации")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/configuration/get",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public OtpConfigurationDTO getConfiguration() {
        log.info("Получение конфигурации");
        return otpConfigurationService.getConfiguration();
    }
}
