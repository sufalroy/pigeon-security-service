package com.skytel.pigeon.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ComponentScan({"com.skytel.pigeon.services"})
public class TaskConfiguration {
    
}
