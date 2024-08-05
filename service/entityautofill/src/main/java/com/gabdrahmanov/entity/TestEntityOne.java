package com.gabdrahmanov.entity;

import com.gabdrahmanov.enums.TestEntityOneEnum;
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
    @OneToOne
    private TestEntityThree problemEntity;

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

    @ManyToOne
    @JoinColumn(name = "previous_entity_id")
    private TestEntityOne previousEntity;

    private TestEntityOneEnum entityOneEnum;
}
