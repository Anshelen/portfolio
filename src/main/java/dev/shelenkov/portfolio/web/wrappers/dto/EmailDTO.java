package dev.shelenkov.portfolio.web.wrappers.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class EmailDTO {

    @NotNull
    @Size(min = 2, max = 30)
    private String name;

    @NotNull
    @Size(min = 2, max = 50)
    private String subject;

    @NotNull
    @Size(max = 999)
    private String text;

    public EmailDTO(String name, String subject, String text) {
        this.name = name;
        this.subject = subject;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
