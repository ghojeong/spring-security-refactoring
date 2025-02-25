package nextstep.security.access;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

// NOTE: https://github.com/spring-projects/spring-security/blob/main/web/src/main/java/org/springframework/security/web/access/AccessDeniedHandlerImpl.java
public final class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) {
        if (!response.isCommitted()) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
        }
    }
}
