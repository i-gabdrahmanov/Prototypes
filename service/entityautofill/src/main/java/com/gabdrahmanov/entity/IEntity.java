package com.gabdrahmanov.entity;

/**
 * Interface for Entity common fields injection
 */
public interface IEntity<T> {

    T getId();

    void setId(T id);
}
