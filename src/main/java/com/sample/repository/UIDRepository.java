package com.sample.repository;

import com.sample.model.UID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UIDRepository extends JpaRepository<UID, Long> {
    @Query(value = "SELECT * FROM uid WHERE model_name=?", nativeQuery = true)
    Optional<UID> findByModelName(String modelName);
}
