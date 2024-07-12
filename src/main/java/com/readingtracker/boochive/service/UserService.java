package com.readingtracker.boochive.service;

import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User saveUser(User user) {
        setEncodedPassword(user); // 비밀번호 해시 변환
        return userRepository.save(user);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    private void setEncodedPassword(User user) {
        user.setPassword(encodePassword(user.getPassword()));
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
