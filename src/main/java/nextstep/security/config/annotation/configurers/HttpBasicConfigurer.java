package nextstep.security.config.annotation.configurers;

import nextstep.security.authentication.AuthenticationManager;
import nextstep.security.authentication.BasicAuthenticationFilter;
import nextstep.security.config.annotation.HttpSecurity;

// NOTE: https://github.com/spring-projects/spring-security/blob/main/config/src/main/java/org/springframework/security/config/annotation/web/configurers/HttpBasicConfigurer.java
public class HttpBasicConfigurer implements SecurityConfigurer {
    @Override
    public void configure(HttpSecurity http) {
        http.addFilter(new BasicAuthenticationFilter(
                http.getSharedObject(AuthenticationManager.class)
        ));
    }
}
