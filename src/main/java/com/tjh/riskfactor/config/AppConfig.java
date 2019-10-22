package com.tjh.riskfactor.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tjh.riskfactor.repo.SaveGuardRepository;
import com.tjh.riskfactor.service.FormService;
import com.tjh.riskfactor.service.AccountService;

import javax.transaction.Transactional;

@Configuration
public class AppConfig {

    /**
     * 启用Jackson的Hibernates LAZY fetch支持
     */
    @Bean
    protected Module module() {
        return new Hibernate5Module();
    }

    /**
     * 初始化数据库数据
     */
    @Bean
    CommandLineRunner runner(FormService forms, AccountService accounts, SaveGuardRepository saveGuards) {
        return new CommandLineRunner() {
            @Override @Transactional
            public void run(String... args) throws Exception {
                if(!saveGuards.existsById(0)) {
                    forms.initDatabase("/data/sections.yml");
                    saveGuards.insert(0);
                }
                if(!saveGuards.existsById(1)) {
                    accounts.initDatabase("/data/user.yml");
                    saveGuards.insert(1);
                }
            }
        };
    }

}
