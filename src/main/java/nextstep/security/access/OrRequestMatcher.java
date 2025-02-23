package nextstep.security.access;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

// NOTE: https://github.com/spring-projects/spring-security/blob/main/web/src/main/java/org/springframework/security/web/util/matcher/OrRequestMatcher.java
public class OrRequestMatcher implements RequestMatcher {
    private final List<RequestMatcher> requestMatchers;

    public OrRequestMatcher(List<RequestMatcher> requestMatchers) {
        this.requestMatchers = requestMatchers;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        for (RequestMatcher matcher : requestMatchers) {
            if (matcher.matches(request)) {
                return true;
            }
        }
        return false;
    }
}
