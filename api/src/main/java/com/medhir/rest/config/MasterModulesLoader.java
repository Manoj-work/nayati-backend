package com.medhir.rest.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;


@Component
@RequiredArgsConstructor
@Slf4j
public class MasterModulesLoader {

    private final ObjectMapper objectMapper;

    @Getter
    private MasterModulesConfig config;

    @PostConstruct
    public void load() {
        try {
            InputStream is = new ClassPathResource("master-modules.json").getInputStream();
            this.config = objectMapper.readValue(is, MasterModulesConfig.class);
            log.info("Loaded Master Modules: {}", this.config);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load master modules JSON", e);
        }
    }
}
