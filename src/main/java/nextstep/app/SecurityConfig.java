package nextstep.app;

import nextstep.oauth2.OAuth2ClientProperties;
import nextstep.oauth2.registration.ClientRegistration;
import nextstep.oauth2.registration.ClientRegistrationRepository;
import nextstep.security.access.hierarchicalroles.RoleHierarchy;
import nextstep.security.access.hierarchicalroles.RoleHierarchyImpl;
import nextstep.security.authorization.SecuredMethodInterceptor;
import nextstep.security.config.Customizer;
import nextstep.security.config.SecurityFilterChain;
import nextstep.security.config.annotation.EnableWebSecurity;
import nextstep.security.config.annotation.HttpSecurity;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.HashMap;
import java.util.Map;


@Configuration
@EnableAspectJAutoProxy
@EnableConfigurationProperties(OAuth2ClientProperties.class)
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                .csrf(c -> c.ignoringRequestMatchers("/login", "/logout"))
                .authorizeHttpRequests(
                        authorizeHttp -> authorizeHttp
                                .requestMatchers("/members").hasRole("ADMIN")
                                .requestMatchers("/members/me").hasRole("USER")
                                .anyRequest().permitAll()
                )
                .httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults())
                .oauth2Login(Customizer.withDefaults())
                .build();
    }

    @Bean
    public SecuredMethodInterceptor securedMethodInterceptor() {
        return new SecuredMethodInterceptor();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.with()
                .role("ADMIN").implies("USER")
                .build();
    }

    @Bean
    ClientRegistrationRepository clientRegistrationRepository(OAuth2ClientProperties properties) {
        final Map<String, ClientRegistration> clientRegistrations = new HashMap<>();
        properties.getRegistration().forEach((registrationId, registration) -> {
            final OAuth2ClientProperties.Provider provider = properties.getProvider().get(registrationId);
            clientRegistrations.put(registrationId, new ClientRegistration(
                    registrationId,
                    registration.getClientId(),
                    registration.getClientSecret(),
                    registration.getRedirectUri(),
                    registration.getScope(),
                    provider.getAuthorizationUri(),
                    provider.getTokenUri(),
                    provider.getUserInfoUri(),
                    provider.getUserNameAttributeName()
            ));
        });
        return new ClientRegistrationRepository(clientRegistrations);
    }
}
