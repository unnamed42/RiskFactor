package com.tjh.riskfactor.security;

import lombok.Data;

import org.springframework.stereotype.Component;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Component
@ConfigurationProperties("security.jwt")
public class JwtProperties {

    /**
     * Top secret used to sign tokens
     */
    private String signingKey;

    private String securityRealm;

    private Integer expiryHours;

    private Integer notBefore;

    private Integer encodingStrength;

}
