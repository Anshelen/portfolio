package dev.shelenkov.portfolio.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
@Data
@NoArgsConstructor
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "country_seq_generator")
    @SequenceGenerator(
        name = "country_seq_generator",
        sequenceName = "country_id_seq",
        allocationSize = 1)
    private Long id;

    @Column(unique = true)
    @Setter(AccessLevel.NONE)
    private String code;

    private String name;

    public Country(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
