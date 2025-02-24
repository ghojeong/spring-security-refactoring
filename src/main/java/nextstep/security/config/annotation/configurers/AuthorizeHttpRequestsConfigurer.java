package nextstep.security.config.annotation.configurers;

import jakarta.servlet.http.HttpServletRequest;
import nextstep.security.access.AnyRequestMatcher;
import nextstep.security.access.MvcRequestMatcher;
import nextstep.security.access.RequestMatcher;
import nextstep.security.access.RequestMatcherEntry;
import nextstep.security.access.hierarchicalroles.NullRoleHierarchy;
import nextstep.security.access.hierarchicalroles.RoleHierarchy;
import nextstep.security.authorization.AuthenticatedAuthorizationManager;
import nextstep.security.authorization.AuthorityAuthorizationManager;
import nextstep.security.authorization.AuthorizationDecision;
import nextstep.security.authorization.AuthorizationFilter;
import nextstep.security.authorization.AuthorizationManager;
import nextstep.security.authorization.RequestMatcherDelegatingAuthorizationManager;
import nextstep.security.config.annotation.HttpSecurity;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// NOTE: https://github.com/spring-projects/spring-security/blob/main/config/src/main/java/org/springframework/security/config/annotation/web/configurers/AuthorizeHttpRequestsConfigurer.java
public class AuthorizeHttpRequestsConfigurer implements SecurityConfigurer {
    private final AuthorizationManagerRequestMatcherRegistry registry;
    private final RoleHierarchy roleHierarchy;

    public AuthorizeHttpRequestsConfigurer(ApplicationContext context) {
        this.registry = new AuthorizationManagerRequestMatcherRegistry();
        this.roleHierarchy = (context.getBeanNamesForType(RoleHierarchy.class).length > 0)
                ? context.getBean(RoleHierarchy.class) : new NullRoleHierarchy();
    }

    @Override
    public void init(HttpSecurity http) {}

    @Override
    public void configure(HttpSecurity http) {
        http.addFilter(new AuthorizationFilter(
                registry.createAuthorizationManager()
        ));
    }

    private AuthorizationManagerRequestMatcherRegistry addMapping(
            List<? extends RequestMatcher> matchers, AuthorizationManager manager
    ) {
        for (RequestMatcher matcher : matchers) {
            registry.addMapping(matcher, manager);
        }
        return registry;
    }

    public AuthorizationManagerRequestMatcherRegistry getRegistry() {
        return registry;
    }

    public class AuthorizationManagerRequestMatcherRegistry {
        private final List<RequestMatcherEntry<AuthorizationManager>> mappings = new ArrayList<>();

        private void addMapping(RequestMatcher matcher, AuthorizationManager manager) {
            mappings.add(new RequestMatcherEntry<>(matcher, manager));
        }

        private AuthorizationManager<HttpServletRequest> createAuthorizationManager() {
            return new RequestMatcherDelegatingAuthorizationManager(mappings);
        }

        public AuthorizedUrl requestMatchers(String... patterns) {
            return requestMatchers(null, patterns);
        }

        public AuthorizedUrl anyRequest() {
            return requestMatchers(AnyRequestMatcher.INSTANCE);
        }

        public AuthorizedUrl requestMatchers(HttpMethod method, String... patterns) {
            final List<RequestMatcher> matchers = new ArrayList<>();
            for (String pattern : patterns) {
                matchers.add(new MvcRequestMatcher(method, pattern));
            }
            return chainRequestMatchers(matchers);
        }

        public AuthorizedUrl requestMatchers(RequestMatcher... requestMatchers) {
            return chainRequestMatchers(Arrays.asList(requestMatchers));
        }

        protected AuthorizedUrl chainRequestMatchers(List<RequestMatcher> requestMatchers) {
            return new AuthorizedUrl(requestMatchers);
        }
    }

    public class AuthorizedUrl {
        private static final AuthorizationManager<?> PERMIT_ALL_MANAGER = (a, o) -> new AuthorizationDecision(true);
        private static final AuthorizationManager<?> AUTHENTICATED_MANAGER = new AuthenticatedAuthorizationManager<>();

        private final List<? extends RequestMatcher> matchers;

        AuthorizedUrl(List<? extends RequestMatcher> matchers) {
            this.matchers = matchers;
        }

        public AuthorizationManagerRequestMatcherRegistry permitAll() {
            return access(PERMIT_ALL_MANAGER);
        }

        public AuthorizationManagerRequestMatcherRegistry hasRole(String role) {
            return access(new AuthorityAuthorizationManager<>(roleHierarchy, role));
        }

        public AuthorizationManagerRequestMatcherRegistry authenticated() {
            return access(AUTHENTICATED_MANAGER);
        }

        public AuthorizationManagerRequestMatcherRegistry access(AuthorizationManager<?> manager) {
            return addMapping(matchers, manager);
        }
    }
}
