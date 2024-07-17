package com.readingtracker.boochive;

import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Slf4j
public class UserCRUDTest {

    @Autowired
    private UserService service;

    @Test
    void createUser() {
        /* given */
        User user = User.builder()
                .email("test3@test.com")
                .password("test")
                .name("테스트 계정")
                .build();

        User savedUser = service.createUser(user);

        /* when, then */
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("test3@test.com");
        assertThat(savedUser.getName()).isEqualTo("테스트 계정");
    }

    @Test
    void updateProfile() {
        /* given */
        User user = User.builder()
                .id(2L)
                .build();

        LocalDate date = LocalDate.of(1998, Month.SEPTEMBER, 29);
        user.updateProfile(null, "테스트 계정 변경", date, 1, "01012345678");

        User savedUser = service.updateUser(2L
                , user);

        /* when, then */
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("test@test.com");
        assertThat(savedUser.getProfileImage()).isNull();
        assertThat(savedUser.getName()).isEqualTo("테스트 계정 변경");
        assertThat(savedUser.getBirthdate()).isEqualTo(date);
        assertThat(savedUser.getSex()).isEqualTo(1);
        assertThat(savedUser.getPhoneNumber()).isEqualTo("01012345678");
    }
}
