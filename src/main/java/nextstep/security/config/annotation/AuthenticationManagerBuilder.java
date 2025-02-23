package nextstep.security.config.annotation;

import nextstep.security.authentication.AuthenticationManager;
import nextstep.security.authentication.AuthenticationProvider;
import nextstep.security.authentication.DaoAuthenticationProvider;
import nextstep.security.authentication.ProviderManager;
import nextstep.security.userdetails.UserDetailsService;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

// NOTE: https://github.com/spring-projects/spring-security/blob/main/config/src/main/java/org/springframework/security/config/annotation/authentication/builders/AuthenticationManagerBuilder.java
public class AuthenticationManagerBuilder {
    private final List<AuthenticationProvider> authenticationProviders = new ArrayList<>();

    public AuthenticationManagerBuilder(ApplicationContext context) {
        final List<UserDetailsService> userDetailsServices = new ArrayList<>(context.getBeansOfType(UserDetailsService.class).values());
        if (userDetailsServices.size() != 1) {
            return;
        }
        authenticationProvider(new DaoAuthenticationProvider(
                userDetailsServices.get(0)
        ));
    }

    public AuthenticationManagerBuilder authenticationProvider(AuthenticationProvider authenticationProvider) {
        authenticationProviders.add(authenticationProvider);
        return this;
    }

    public AuthenticationManager build() {
        ProviderManager providerManager = new ProviderManager(this.authenticationProviders);
        return providerManager;
    }
}
