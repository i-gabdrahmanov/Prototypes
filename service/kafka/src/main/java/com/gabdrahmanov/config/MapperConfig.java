package com.gabdrahmanov.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gabdrahmanov.beanmodifier.BeanDeserializerModifierWithValidation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    /**
     * Бин бля настройки валидации через аннотации через objectMapper
     * @return бин ObjectMapper
     */
    @Bean
    public ObjectMapper objectMapper() {
        SimpleModule validationModule = new SimpleModule();
        validationModule.setDeserializerModifier(new BeanDeserializerModifierWithValidation());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(validationModule);
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}

