package nextstep.security.access;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

// NOTE: https://github.com/spring-projects/spring-security/blob/main/web/src/main/java/org/springframework/security/web/util/matcher/AndRequestMatcher.java
public class AndRequestMatcher implements RequestMatcher {
    private final List<RequestMatcher> requestMatchers;

    public AndRequestMatcher(List<RequestMatcher> requestMatchers) {
        this.requestMatchers = requestMatchers;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        for (RequestMatcher matcher : requestMatchers) {
            if (!matcher.matches(request)) {
                return false;
            }
        }
        return true;
    }
}
