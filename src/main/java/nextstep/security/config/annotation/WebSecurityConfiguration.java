package nextstep.security.config.annotation;

import jakarta.servlet.Filter;
import nextstep.security.config.Customizer;
import nextstep.security.config.FilterChainProxy;
import nextstep.security.config.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

// NOTE: https://github.com/spring-projects/spring-security/blob/main/config/src/main/java/org/springframework/security/config/annotation/web/configuration/WebSecurityConfiguration.java
@Configuration(proxyBeanMethods = false)
public class WebSecurityConfiguration {
    private List<SecurityFilterChain> securityFilterChains = new ArrayList<>();

    @Autowired(required = false)
    private HttpSecurity httpSecurity;

    @Autowired(required = false)
    void setFilterChains(List<SecurityFilterChain> securityFilterChains) {
        this.securityFilterChains = securityFilterChains;
    }

    @Bean(name = "springSecurityFilterChain")
    public Filter springSecurityFilterChain() {
        if (securityFilterChains.isEmpty() && httpSecurity != null) {
            httpSecurity.authorizeHttpRequests((authorize) -> authorize.anyRequest().authenticated());
            httpSecurity.formLogin(Customizer.withDefaults());
            httpSecurity.httpBasic(Customizer.withDefaults());
            securityFilterChains.add(httpSecurity.build());
        }
        return new FilterChainProxy(securityFilterChains);
    }
}
