package com.learnspring.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "cards")
@Getter
@Setter
public class CardsContactInfo {
    private String message;
    private Map<String, String> contactInfo;
    private Boolean onboarding;

    // Getters and setters
}
