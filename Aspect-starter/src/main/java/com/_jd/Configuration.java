package com._jd;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@org.springframework.context.annotation.Configuration
public class Configuration {
    @Bean
    public AuditionAspect aspect(){
        return new AuditionAspect();
    }

}
