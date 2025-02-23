package nextstep.security.access;

import jakarta.servlet.http.HttpServletRequest;

// NOTE: https://github.com/spring-projects/spring-security/blob/main/web/src/main/java/org/springframework/security/web/util/matcher/NegatedRequestMatcher.java
public class NegatedRequestMatcher implements RequestMatcher {
    private final RequestMatcher requestMatcher;

    public NegatedRequestMatcher(RequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        return !requestMatcher.matches(request);
    }
}
