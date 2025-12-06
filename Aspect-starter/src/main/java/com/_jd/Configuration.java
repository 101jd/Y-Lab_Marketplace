package com._jd;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@EnableAspectJAutoProxy
@org.springframework.context.annotation.Configuration
public class Configuration {
    @Bean
    public AuditionAspect aspect(){
        return new AuditionAspect();
    }

    @Bean
    public AuditionHandler handlerAspect(SaveRepository<Long, AuditionEntity> repository){
        return new AuditionService(repository);
    }

    @Bean SaveRepository<Long, AuditionEntity> repositoryAspect(JdbcTemplate template){
        return new AuditionRepository(template);
    }

    @Bean
    JdbcTemplate templateAspect(DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }

}
