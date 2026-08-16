package com.los.loanoriginatingsystem.documentgeneration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

/**
 * A dedicated Thymeleaf TemplateEngine for merging DB-stored document
 * templates (StringTemplateResolver reads the template text passed in
 * directly, not from classpath files). Kept separate from Spring Boot's
 * auto-configured MVC template engine (which resolves views from
 * classpath:/templates/) so this REST API's document-generation feature
 * doesn't interact with view resolution at all.
 */
@Configuration
public class DocumentTemplateEngineConfig {

    @Bean(name = "documentTemplateEngine")
    public TemplateEngine documentTemplateEngine() {
        StringTemplateResolver resolver = new StringTemplateResolver();
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCacheable(false);

        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);
        return engine;
    }
}
