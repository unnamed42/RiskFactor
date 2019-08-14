package com.tjh.riskfactor.security;

import lombok.Data;

import org.springframework.stereotype.Component;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Component
@ConfigurationProperties(
    prefix = "security.jwt"
)
public class JwtProperties {

    private String signingKey;
    private String securityRealm;
    private String claimedProperty;
    private Integer expiryHours;

}
