package com.tjh.riskfactor.enums;

import lombok.Getter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserStatus {

    ACTIVE(0),
    DISABLED(1);

    @Getter private final int code;

}
