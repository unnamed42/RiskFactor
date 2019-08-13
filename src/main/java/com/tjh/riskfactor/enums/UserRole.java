package com.tjh.riskfactor.enums;

import lombok.Getter;
import lombok.AllArgsConstructor;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;
import java.util.stream.Collectors;

@AllArgsConstructor
public enum UserRole {

    NORMAL(0),
    ADMIN(1000);

    @Getter private final int code;

    public static Stream<String> getRoles() {
        return Arrays.stream(UserRole.class.getEnumConstants())
                .map(Enum::name);
    }

    public static Collection<SimpleGrantedAuthority> getAuthorities() {
        return getRoles().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

}
