package com.scritorrelo.repository;

import java.util.Optional;
import java.util.UUID;

import com.scritorrelo.zello.message.Location;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends CrudRepository<Location, UUID> {

    Optional<Location> findById(int id);

    Optional<Location> findByUuid(UUID uuid);
}