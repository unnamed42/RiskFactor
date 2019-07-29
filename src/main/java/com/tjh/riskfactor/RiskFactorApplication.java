package com.tjh.riskfactor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
    // disable page and auto-redirect to /login
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
})
public class RiskFactorApplication {

    public static void main(String[] args) {
        SpringApplication.run(RiskFactorApplication.class, args);
    }

}
