package com.readingtracker.boochive.repository;

import com.readingtracker.boochive.domain.UserConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserConfigRepository extends JpaRepository<UserConfig, Long> {

    Optional<UserConfig> findByUserIdAndConfigKey(Long userId, String configKey);

    List<UserConfig> findAllByUserId(Long userId);
}
