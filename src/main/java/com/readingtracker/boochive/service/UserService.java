package com.readingtracker.boochive.service;

import com.readingtracker.boochive.domain.User;
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
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * [C]R[U]D - CREATE/UPDATE
     */
    @Transactional
    public User saveUser(User user) {
        if (user.getPassword() != null) {
            user.updatePassword(passwordEncoder.encode(user.getPassword())); // 비밀번호 해시 변환
        }

        if (user.getId() != null) { // update
            User existingUser = userRepository.findById(user.getId()).orElseThrow();

            if (user.getPassword() != null) {
                existingUser.updatePassword(user.getPassword());
            }
            if (user.getName() != null) {
                existingUser.updateProfile(
                        user.getProfileImage(),
                        user.getName(),
                        user.getBirthdate(),
                        user.getSex(),
                        user.getPhoneNumber()
                );
            }

            return existingUser;
        }

        return userRepository.save(user); // insert
    }

    /**
     * CRU[D] - DELETE
     */
    @Transactional
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}
