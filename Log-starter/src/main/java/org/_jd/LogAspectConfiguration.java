package org._jd;

import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class LogAspectConfiguration {
    @Bean
    public LoggingAspect loggingAspect(){
        return new LoggingAspect();
    }
}
