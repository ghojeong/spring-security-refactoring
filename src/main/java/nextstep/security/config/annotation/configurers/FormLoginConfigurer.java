package nextstep.security.config.annotation.configurers;

import nextstep.security.authentication.AuthenticationManager;
import nextstep.security.authentication.UsernamePasswordAuthenticationFilter;
import nextstep.security.config.annotation.HttpSecurity;

// NOTE: https://github.com/spring-projects/spring-security/blob/main/config/src/main/java/org/springframework/security/config/annotation/web/configurers/FormLoginConfigurer.java
public class FormLoginConfigurer implements SecurityConfigurer {
    @Override
    public void init(HttpSecurity http) {}

    @Override
    public void configure(HttpSecurity http) {
        http.addFilter(new UsernamePasswordAuthenticationFilter(
                http.getSharedObject(AuthenticationManager.class)
        ));
    }
}
