package nextstep.security.web.csrf;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.UUID;

// NOTE: https://github.com/spring-projects/spring-security/blob/main/web/src/main/java/org/springframework/security/web/csrf/HttpSessionCsrfTokenRepository.java
public final class HttpSessionCsrfTokenRepository implements CsrfTokenRepository {
    private static final String PARAMETER_NAME = "_csrf";
    private static final String HEADER_NAME = "X-CSRF-TOKEN";
    private static final String ATTRIBUTE_NAME = HttpSessionCsrfTokenRepository.class
            .getName().concat(".CSRF_TOKEN");

    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        return new CsrfToken(HEADER_NAME, PARAMETER_NAME, UUID.randomUUID().toString());
    }

    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
        if (token != null) {
            request.getSession().setAttribute(ATTRIBUTE_NAME, token);
            return;
        }
        final HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(ATTRIBUTE_NAME);
        }
    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        final HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (CsrfToken) session.getAttribute(ATTRIBUTE_NAME);
    }
}
