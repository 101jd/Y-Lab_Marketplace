package org._jd;

import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class Configuration {
    @Bean
    public LoggingAspect aspect(){
        return new LoggingAspect();
    }
}
