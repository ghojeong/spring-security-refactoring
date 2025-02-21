package nextstep.security.web.csrf;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// NOTE: https://github.com/spring-projects/spring-security/blob/main/web/src/main/java/org/springframework/security/web/csrf/CsrfTokenRepository.java
public interface CsrfTokenRepository {
    CsrfToken generateToken(HttpServletRequest request);

    void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response);

    CsrfToken loadToken(HttpServletRequest request);
}
