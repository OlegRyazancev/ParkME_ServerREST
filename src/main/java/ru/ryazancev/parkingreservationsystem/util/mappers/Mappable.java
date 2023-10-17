package ru.ryazancev.parkingreservationsystem.util.mappers;

import java.util.List;

public interface Mappable<E, D> {

    D toDTO(E entity);

    List<D> toDTO(List<E> entity);

    E toEntity(D dto);

    List<E> toEntity(List<D> dto);
}
