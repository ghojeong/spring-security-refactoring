package nextstep.security.web.csrf;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.access.AccessDeniedHandler;
import nextstep.security.access.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

// NOTE: https://github.com/spring-projects/spring-security/blob/main/web/src/main/java/org/springframework/security/web/access/AccessDeniedHandler.java
public class CsrfFilter extends OncePerRequestFilter {
    public static final RequestMatcher DEFAULT_CSRF_MATCHER = DefaultRequiresCsrfMatcher.INSTANCE;

    private final AccessDeniedHandler accessDeniedHandler;
    private final CsrfTokenRepository tokenRepository;

    private RequestMatcher csrfMatcher = DEFAULT_CSRF_MATCHER;

    public CsrfFilter(AccessDeniedHandler accessDeniedHandler, CsrfTokenRepository tokenRepository) {
        this.accessDeniedHandler = accessDeniedHandler;
        this.tokenRepository = tokenRepository;
    }

    public void setRequireCsrfProtectionMatcher(RequestMatcher requireCsrfProtectionMatcher) {
        csrfMatcher = requireCsrfProtectionMatcher;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final CsrfToken csrfToken = getCsrfToken(request, response);
        request.setAttribute(CsrfToken.class.getName(), csrfToken);
        if (!csrfMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        final String actualToken = getActualToken(request, csrfToken);
        if (!Objects.equals(csrfToken.token(), actualToken)) {
            accessDeniedHandler.handle(request, response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private CsrfToken getCsrfToken(HttpServletRequest request, HttpServletResponse response) {
        final CsrfToken loadedToken = tokenRepository.loadToken(request);
        if (loadedToken != null) {
            return loadedToken;
        }
        final CsrfToken generatedToken = tokenRepository.generateToken(request);
        tokenRepository.saveToken(generatedToken, request, response);
        return generatedToken;
    }

    private String getActualToken(HttpServletRequest request, CsrfToken csrfToken) {
        final String actualToken = request.getHeader(csrfToken.headerName());
        return actualToken != null
                ? actualToken
                : request.getParameter(csrfToken.parameterName());
    }

    // NOTE: https://github.com/spring-projects/spring-security/blob/4f25f0b90f66bfa99e763ebe7a6454342931d273/web/src/main/java/org/springframework/security/web/csrf/CsrfFilter.java#L203
    private static final class DefaultRequiresCsrfMatcher implements RequestMatcher {
        private static final DefaultRequiresCsrfMatcher INSTANCE = new DefaultRequiresCsrfMatcher();
        private final Set<String> allowedMethods = Set.of("GET", "HEAD", "TRACE", "OPTIONS");

        private DefaultRequiresCsrfMatcher() {}

        @Override
        public boolean matches(HttpServletRequest request) {
            return !allowedMethods.contains(request.getMethod());
        }
    }
}
