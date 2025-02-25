package nextstep.security.config.annotation.configurers;

import nextstep.security.access.AccessDeniedHandler;
import nextstep.security.access.AndRequestMatcher;
import nextstep.security.access.MvcRequestMatcher;
import nextstep.security.access.NegatedRequestMatcher;
import nextstep.security.access.OrRequestMatcher;
import nextstep.security.access.RequestMatcher;
import nextstep.security.config.annotation.HttpSecurity;
import nextstep.security.web.csrf.CsrfFilter;
import nextstep.security.web.csrf.CsrfTokenRepository;

import java.util.ArrayList;
import java.util.List;

// NOTE: https://github.com/spring-projects/spring-security/blob/main/config/src/main/java/org/springframework/security/config/annotation/web/configurers/CsrfConfigurer.java
public class CsrfConfigurer implements SecurityConfigurer {
    private final RequestMatcher requireCsrfProtectionMatcher = CsrfFilter.DEFAULT_CSRF_MATCHER;
    private final List<RequestMatcher> ignoredCsrfProtectionMatchers = new ArrayList<>();

    private final AccessDeniedHandler accessDeniedHandler;
    private final CsrfTokenRepository csrfTokenRepository;

    public CsrfConfigurer(AccessDeniedHandler accessDeniedHandler, CsrfTokenRepository csrfTokenRepository) {
        this.accessDeniedHandler = accessDeniedHandler;
        this.csrfTokenRepository = csrfTokenRepository;
    }

    @Override
    public void init(HttpSecurity http) {}

    @Override
    public void configure(HttpSecurity http) {
        final CsrfFilter filter = new CsrfFilter(
                accessDeniedHandler,
                csrfTokenRepository
        );
        final RequestMatcher requireCsrfProtectionMatcher = getRequireCsrfProtectionMatcher();
        if (requireCsrfProtectionMatcher != null) {
            filter.setRequireCsrfProtectionMatcher(requireCsrfProtectionMatcher);
        }
        http.addFilter(filter);
    }

    private RequestMatcher getRequireCsrfProtectionMatcher() {
        if (ignoredCsrfProtectionMatchers.isEmpty()) {
            return requireCsrfProtectionMatcher;
        }
        return new AndRequestMatcher(List.of(
                requireCsrfProtectionMatcher,
                new NegatedRequestMatcher(
                        new OrRequestMatcher(ignoredCsrfProtectionMatchers)
                )
        ));
    }

    public CsrfConfigurer ignoringRequestMatchers(String... patterns) {
        for (String pattern : patterns) {
            ignoredCsrfProtectionMatchers.add(
                    new MvcRequestMatcher(null, pattern)
            );
        }
        return this;
    }
}
