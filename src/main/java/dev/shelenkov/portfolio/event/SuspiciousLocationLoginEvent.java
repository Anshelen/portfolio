package dev.shelenkov.portfolio.event;

import dev.shelenkov.portfolio.domain.Account;
import dev.shelenkov.portfolio.domain.Country;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ToString
@Getter
@RequiredArgsConstructor
public class SuspiciousLocationLoginEvent {

    @NotNull
    private final Account account;
    @NotBlank
    private final String ip;
    @NotNull
    private final Country country;
}
