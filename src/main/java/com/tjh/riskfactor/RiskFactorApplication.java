package com.tjh.riskfactor;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.val;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.tjh.riskfactor.entity.form.Section;
import com.tjh.riskfactor.service.FormService;
import com.tjh.riskfactor.repo.SaveGuardRepository;

import java.util.List;
import javax.transaction.Transactional;

@SpringBootApplication
public class RiskFactorApplication {

    public static void main(String[] args) {
        SpringApplication.run(RiskFactorApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(FormService service, SaveGuardRepository saveGuards) {
        return new CommandLineRunner() {
            @Override @Transactional
            public void run(String... args) throws Exception {
                if(saveGuards.existsById(0))
                    return;
                val mapper = new ObjectMapper(new YAMLFactory());
                mapper.findAndRegisterModules();
                val type = new TypeReference<List<Section>>() {};
                try(val is = TypeReference.class.getResourceAsStream("/schema.yml")) {
                    List<Section> sections = mapper.readValue(is, type);
                    sections.forEach(service::saveSection);
                }
                saveGuards.insert(0);
            }
        };
    }

}
