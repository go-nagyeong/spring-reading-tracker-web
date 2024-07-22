package com.readingtracker.boochive.domain;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@SQLDelete(sql = "UPDATE users SET deleted_at = CURRENT_TIMESTAMP WHERE id=?")
@Where(clause = "deleted_at IS NULL")
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 50, nullable = false)
    private String name;

    private String profileImage;

    @Column(insertable = false, nullable = false)
    @ColumnDefault("0")
    private Integer sex;

    private LocalDate birthdate;

    @Column(length = 15)
    private String phoneNumber;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    /**
     * 비밀번호 변경
     */
    public void updatePassword(String password) {
        this.password = password;
    }

    /**
     * 프로필 정보 변경
     */
    public void updateProfile(String profileImage, String name, LocalDate birthdate, Integer sex, String phoneNumber) {
        this.profileImage = profileImage;
        this.name = name;
        this.birthdate = birthdate;
        this.sex = sex;
        this.phoneNumber = phoneNumber;
    }

    /**
     * UserDetails 상속 - Getter
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    /**
     * [UserDetails 상속] 계정 만료 여부 => 사용 X
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * [UserDetails 상속] 계정 잠김 여부 => TODO: 추후 비밀번호 연속 틀릴 시 계정 잠그는 기능 추가
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * [UserDetails 상속] 비밀번호 만료 여부 => TODO: 추후 비밀번호 변경 일자를 저장하고 그 일자를 기준으로 비밀번호 유효 기간을 판단하는 기능 개발
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * [UserDetails 상속] 계정 활성화 여부 => 사용 X
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
