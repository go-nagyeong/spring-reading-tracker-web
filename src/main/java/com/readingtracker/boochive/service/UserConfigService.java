package com.readingtracker.boochive.service;

import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.domain.UserConfig;
import com.readingtracker.boochive.repository.UserConfigRepository;
import com.readingtracker.boochive.util.ResourceAccessUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserConfigService {

    private final UserConfigRepository userConfigRepository;
    private final ResourceAccessUtil<UserConfig> resourceAccessUtil;

    /**
     * C[R]UD - READ
     */
    @Transactional(readOnly = true)
    public Optional<UserConfig> findConfigByUserAndKey(Long userId, String key) {
        return userConfigRepository.findByUserIdAndConfigKey(userId, key);
    }

    @Transactional(readOnly = true)
    public List<UserConfig> getConfigsByUser(Long userId) {
        return userConfigRepository.findAllByUserId(userId);
    }

    /**
     * [C]RUD - CREATE
     */
    @Transactional
    public UserConfig createConfig(UserConfig userConfig) {
        return userConfigRepository.save(userConfig);
    }

    /**
     * CR[U]D - UPDATE
     */
    @Transactional
    public UserConfig updateConfig(Long id, UserConfig userConfig) {
        UserConfig existingUserConfig = resourceAccessUtil.checkAccessAndRetrieve(id);

        existingUserConfig.updateConfigValue(userConfig.getConfigValue());

        return existingUserConfig;
    }

    /**
     * [C]R[U]D - BATCH CREATE/UPDATE
     */
    @Transactional
    public Map<String, String> handleConfigs(Map<String, String> configs, User user) {
        List<UserConfig> updatedConfigList = new ArrayList<>();

        for (Map.Entry<String, String> entry : configs.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            Optional<UserConfig> existingConfig = userConfigRepository.findByUserIdAndConfigKey(user.getId(), key);
            existingConfig.ifPresentOrElse(
                    config -> {
                        config.updateConfigValue(value);
                        updatedConfigList.add(config);
                    },
                    () -> {
                        UserConfig newConfig = UserConfig.builder()
                                .user(user)
                                .configKey(key)
                                .configValue(value)
                                .build();
                        updatedConfigList.add(userConfigRepository.save(newConfig));
                    }
            );
        }

        return updatedConfigList.stream()
                .collect(Collectors.toMap(UserConfig::getConfigKey, UserConfig::getConfigValue));
    }

    /**
     * CRU[D] - DELETE
     */
    @Transactional
    public void deleteConfig(Long id) {
        UserConfig existingUserConfig = resourceAccessUtil.checkAccessAndRetrieve(id);

        userConfigRepository.delete(existingUserConfig);
    }
}
