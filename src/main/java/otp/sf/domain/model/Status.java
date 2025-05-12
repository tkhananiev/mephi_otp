package otp.sf.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Статус OTP-кода
 */
@RequiredArgsConstructor
public enum Status {

    ACTIVE("Активен"),
    EXPIRED("Просрочен"),
    USED("Использован");

    @Getter(onMethod_ = @JsonValue)
    private final String name;

    private static final Map<String, Status> MAP = Stream.of(values())
            .collect(Collectors.toMap(Status::getName, Function.identity()));

    @JsonCreator
    public static Status forValue(final String value) {
        return MAP.get(value);
    }
}
