package nextstep.security.config.annotation.configurers;

import nextstep.security.config.annotation.HttpSecurity;

// NOTE: https://github.com/spring-projects/spring-security/blob/main/config/src/main/java/org/springframework/security/config/annotation/SecurityConfigurer.java
public interface SecurityConfigurer {
    void init(HttpSecurity http);

    void configure(HttpSecurity http);
}
