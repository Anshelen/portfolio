package dev.shelenkov.portfolio.web.wrappers.dto;

import lombok.Value;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Value
public class EmailDTO {

    @NotNull
    @Size(min = 2, max = 30)
    private final String name;

    @NotNull
    @Size(min = 2, max = 50)
    private final String subject;

    @NotNull
    @Size(max = 999)
    private final String text;
}
