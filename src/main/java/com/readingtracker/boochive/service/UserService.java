package com.readingtracker.boochive.service;

import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.dto.user.PasswordUpdateRequest;
import com.readingtracker.boochive.dto.auth.RegisterRequest;
import com.readingtracker.boochive.dto.user.UserInfoRequest;
import com.readingtracker.boochive.dto.user.UserInfoResponse;
import com.readingtracker.boochive.exception.ResourceNotFoundException;
import com.readingtracker.boochive.mapper.RegisterMapper;
import com.readingtracker.boochive.mapper.UserInfoMapper;
import com.readingtracker.boochive.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * C[R]UD - READ
     */
    @Transactional(readOnly = true)
    public Optional<UserInfoResponse> findUserByEmail(String email) {
        return userRepository.findByEmailAndDeletedAtIsNull(email)
                .map(UserInfoMapper.INSTANCE::toDto);
    }

    // 탈퇴한 계정도 포함해서 조회
    @Transactional(readOnly = true)
    public Optional<UserInfoResponse> findUserByEmailIncludingDeleted(String email) {
        return userRepository.findByEmail(email)
                .map(UserInfoMapper.INSTANCE::toDto);
    }

    /**
     * [C]RUD - CREATE
     */
    @Transactional
    public UserInfoResponse createUser(RegisterRequest request) {
        // 비밀번호 해시 변환
        request.setPassword(passwordEncoder.encode(request.getPassword()));

        User newUser = RegisterMapper.INSTANCE.toEntity(request);

        return UserInfoMapper.INSTANCE.toDto(userRepository.save(newUser));
    }

    /**
     * CR[U]D - UPDATE
     */
    @Transactional
    public UserInfoResponse updateUser(Long id, UserInfoRequest userInfo) {
        User existingUser = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("회원"));

        existingUser.updateProfile(
                userInfo.getUsername(),
                userInfo.getProfileImage(),
                userInfo.getSex(),
                LocalDate.parse(userInfo.getBirthdate()),
                userInfo.getPhoneNumber()
        );

        return UserInfoMapper.INSTANCE.toDto(existingUser);
    }

    @Transactional
    public void updatePassword(Long id, PasswordUpdateRequest request) {
        User existingUser = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("회원"));

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(request.getOriginPassword(), existingUser.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 올바르지 않습니다.");
        }

        // 비밀번호 해시 변환
        request.setPassword(passwordEncoder.encode(request.getPassword()));

        existingUser.updatePassword(request.getPassword());
    }

    /**
     * CRU[D] - DELETE
     */
    @Transactional
    public void deleteUserById(Long id) {
        // 데이터 존재 여부 검사
        User existingUser = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("회원"));

        userRepository.delete(existingUser);
    }
}
