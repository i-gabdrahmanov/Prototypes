package com.gabdrahmanov.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
public abstract class RegistryEntity implements IEntity<Long> {

    @Id
    @Column
  //  @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
}
