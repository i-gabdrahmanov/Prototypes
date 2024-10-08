package com.gabdrahmanov.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "test_entity_two")
public class TestEntityTwo extends RegistryEntity {

    @Column
    private String name;

    @Column
    private Instant stardDate;

    @ManyToOne
    private TestEntityOne entityOne;
}
