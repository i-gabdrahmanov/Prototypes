package com.gabdrahmanov.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "test_entity_one")
public class TestEntityOne extends RegistryEntity {

    @Column
    private String name;

    @Column
    private int cost;

    @OneToOne
    private TestEntityTwo previous;

    @ManyToOne
    @JoinColumn
    private TestEntityTwo testEntityTwo;

    @OneToMany
    @JoinColumn
    private Collection<TestEntityThree> entityThreeCollection;

    @OneToMany
    @JoinColumn
    private Set<TestEntityTwo> entityTwoSet;

    @Column
    private Instant instant;

    @Column
    private LocalDate localDate;

    @Column
    private ZonedDateTime zonedDateTime;

    @Column
    private Integer integer;
}
