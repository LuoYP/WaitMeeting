package org.example.configuration;

import org.example.common.annotation.Bean;
import org.example.common.annotation.Configuration;
import org.example.schedule.TimeWheel;

@Configuration
public class Bootstrap {

    @Bean
    public TimeWheel timeWheel() {
        return new TimeWheel();
    }
}
