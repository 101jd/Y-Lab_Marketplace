package org.y_lab.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import liquibase.integration.spring.SpringLiquibase;
import org._jd.EnableLogAspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;

@EnableWebMvc
@Configuration
@ComponentScan("org.y_lab")
@PropertySource(value = "classpath:./application.properties")
@EnableAspectJAutoProxy
@OpenAPIDefinition
@EnableLogAspect
@EnableAutoConfiguration
public class WebAppConfig implements WebMvcConfigurer {

    @Bean
    SpringLiquibase liquibase(DataSource dataSource){
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setDefaultSchema("mps");

        liquibase.setShouldRun(true);
        liquibase.setChangeLog("db/changelog/changelog.xml");

        return liquibase;
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("schema.sql"));

        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(populator);
        return initializer;
    }

    @Primary
    @Bean
    JdbcTemplate jdbcTemplate(DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }

    @Bean
    OpenAPI openAPI(){
        OpenAPI openAPI = new OpenAPI();
        openAPI.info(new Info()
                .title("Marketplace API").version("0.4"));
        return openAPI;
    }

}
