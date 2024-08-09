package com.readingtracker.boochive.controller;

import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.domain.UserConfig;
import com.readingtracker.boochive.service.UserConfigService;
import com.readingtracker.boochive.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user-configs")
@RequiredArgsConstructor
public class UserConfigController {

    private final UserConfigService userConfigService;

    /**
     * GET - 로그인 유저의 Config 전체 조회
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, String>>> getUserConfigs(@AuthenticationPrincipal User user) {
        Map<String, String> data = userConfigService.getConfigsByUser(user.getId())
                .stream()
                .collect(Collectors.toMap(UserConfig::getConfigKey, UserConfig::getConfigValue));

        return ApiResponse.success(null, data);
    }

    /**
     * POST - Config 생성 및 변경
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, String>>> createOrUpdateUserConfig(@RequestBody Map<String, String> configs,
                                                                                     @AuthenticationPrincipal User user) {
        Map<String, String> updatedConfigList = userConfigService.handleConfigs(configs, user.getId());

        return ApiResponse.success(null, updatedConfigList);
    }
}
