package com.crossfit.pieds_croises.datetime;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DateTimeProvider {

    private final Clock clock;

    public LocalDateTime now() {
        return LocalDateTime.now(clock);
    }

    public LocalDate today() { return LocalDate.now(clock); }
}
