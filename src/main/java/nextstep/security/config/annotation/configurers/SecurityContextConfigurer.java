package nextstep.security.config.annotation.configurers;

import nextstep.security.config.annotation.HttpSecurity;
import nextstep.security.context.SecurityContextHolderFilter;

// NOTE: https://github.com/spring-projects/spring-security/blob/main/config/src/main/java/org/springframework/security/config/annotation/web/configurers/SecurityContextConfigurer.java
public class SecurityContextConfigurer implements SecurityConfigurer {
    @Override
    public void configure(HttpSecurity http) {
        http.addFilter(new SecurityContextHolderFilter());
    }
}
