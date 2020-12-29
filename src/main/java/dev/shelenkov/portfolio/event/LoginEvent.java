package dev.shelenkov.portfolio.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@ToString
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class LoginEvent {

    @Positive
    private final long accountId;
    @NotBlank
    private final String ip;
}
