package com.books.api.repository;

import com.books.api.model.Config;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfigRepository extends JpaRepository<Config, Long> {
    Optional<Config> findByVarName(String varName);
}
