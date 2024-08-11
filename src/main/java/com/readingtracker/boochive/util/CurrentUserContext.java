package com.readingtracker.boochive.util;

import com.readingtracker.boochive.domain.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class CurrentUserContext {

    private static final ThreadLocal<User> currentUser = ThreadLocal.withInitial(() -> {
        // 기본값으로 현재 인증된 유저를 가져옵니다.
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    });

    public static User getUser() {
        return currentUser.get();
    }

    public static void setUser(User user) {
        currentUser.set(user);
    }

    public static void clear() {
        currentUser.remove();
    }
}
