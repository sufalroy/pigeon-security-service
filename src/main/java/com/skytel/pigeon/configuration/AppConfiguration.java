package com.skytel.pigeon.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.skytel.pigeon.security.ActiveUserStore;

@Configuration
public class AppConfiguration {

    @Bean
    public ActiveUserStore activeUserStore() {
        return new ActiveUserStore();
    }
}
