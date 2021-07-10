package com.scritorrelo.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.scritorrelo.zello.message.Text;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TextRepository extends CrudRepository<Text, UUID> {

    Optional<Text> findById(int id);

    Optional<Text> findByUuid(UUID uuid);

    @NotNull Text save(@NotNull Text text);

    List<Text> findAll();
}