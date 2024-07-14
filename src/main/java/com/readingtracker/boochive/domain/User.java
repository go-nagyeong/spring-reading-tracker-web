package com.readingtracker.boochive.domain;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@SQLDelete(sql = "UPDATE users SET deleted_at = CURRENT_TIMESTAMP WHERE id=?")
@Where(clause = "deleted_at IS NULL")
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Column(length = 255, nullable = false)
    private String password;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 255)
    private String profileImage;

    @Column(columnDefinition = "CHECK (sex IN (0, 1, 2, 9))")
    @ColumnDefault("0")
    private Integer sex;

    @Column
    private Date birthdate;

    @Column(length = 15)
    private String phoneNumber;

    @Column
    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column
    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

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
     * 계정 만료 여부
     * => 사용 X
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 계정 잠김 여부
     * => TODO: 추후 비밀번호 연속 틀릴 시 계정 잠그는 기능 추가
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 비밀번호 만료 여부
     * => TODO: 추후 비밀번호 변경 일자를 저장하고 그 일자를 기준으로 비밀번호 유효 기간을 판단하는 기능 개발
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 계정 활성화 여부
     * => 사용 X
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
