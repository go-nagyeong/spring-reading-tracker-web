package com.readingtracker.boochive.service;

import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.dto.PasswordDto;
import com.readingtracker.boochive.dto.UserDto;
import com.readingtracker.boochive.mapper.UserMapper;
import com.readingtracker.boochive.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Optional<UserDto> findUserById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper.INSTANCE::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<UserDto> findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserMapper.INSTANCE::toDto);
    }

    /**
     * [C]RUD - CREATE
     */
    @Transactional
    public User createUser(User user) {
        user.updatePassword(passwordEncoder.encode(user.getPassword())); // 비밀번호 해시 변환

        return userRepository.save(user);
    }

    /**
     * CR[U]D - UPDATE
     */
    @Transactional
    public UserDto updateUser(Long id, UserDto user) {
        User existingUser = userRepository.findById(id).orElseThrow();

        existingUser.updateProfile(
                user.getProfileImage(),
                user.getUsername(),
                user.getBirthdate(),
                user.getSex(),
                user.getPhoneNumber()
        );

        return UserMapper.INSTANCE.toDto(existingUser);
    }

    @Transactional
    public void updatePassword(Long id, PasswordDto passwordDto) {
        User existingUser = userRepository.findById(id).orElseThrow();

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(passwordDto.getOriginPassword(), existingUser.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 올바르지 않습니다.");
        }

        // 새 비밀번호 설정
        existingUser.updatePassword(passwordEncoder.encode(passwordDto.getPassword()));
    }

    /**
     * CRU[D] - DELETE
     */
    @Transactional
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}
