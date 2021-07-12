package com.scritorrelo.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.scritorrelo.zello.message.Text;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface TextRepository extends CrudRepository<Text, UUID> {

    Optional<Text> findById(int id);

    Optional<Text> findByUuid(UUID uuid);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @NotNull <S extends Text> S save(@NotNull S text);

    @NotNull List<Text> findAll();
}