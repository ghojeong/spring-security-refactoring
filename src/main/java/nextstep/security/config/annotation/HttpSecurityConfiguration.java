package nextstep.security.config.annotation;

import nextstep.security.config.Customizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.Map;

// NOTE: https://github.com/spring-projects/spring-security/blob/main/config/src/main/java/org/springframework/security/config/annotation/web/configuration/HttpSecurityConfiguration.java
@Configuration
public class HttpSecurityConfiguration {
    @Autowired
    private ApplicationContext context;

    @Bean("nextstep.security.config.annotation.HttpSecurityConfiguration.httpSecurity")
    @Scope("prototype")
    HttpSecurity httpSecurity() {
        return new HttpSecurity(
                new AuthenticationManagerBuilder(context),
                Map.of(ApplicationContext.class, this.context)
        ).securityContext(Customizer.withDefaults());
    }
}
