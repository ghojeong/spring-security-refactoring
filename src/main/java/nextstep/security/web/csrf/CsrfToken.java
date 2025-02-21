package nextstep.security.web.csrf;

// NOTE: https://github.com/spring-projects/spring-security/blob/main/web/src/main/java/org/springframework/security/web/csrf/CsrfToken.java
public record CsrfToken(
        String headerName,
        String parameterName,
        String token
) {}
