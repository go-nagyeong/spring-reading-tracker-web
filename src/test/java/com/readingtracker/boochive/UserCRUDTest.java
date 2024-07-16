package com.readingtracker.boochive;

import com.readingtracker.boochive.domain.User;
import com.readingtracker.boochive.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

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

        User savedUser = service.saveUser(user);

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

        Date date = new Date();
        date.setTime(907036813);
        user.updateProfile(null, "테스트 계정 변경", date, 1, "01012345678");

        User savedUser = service.saveUser(user);

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
