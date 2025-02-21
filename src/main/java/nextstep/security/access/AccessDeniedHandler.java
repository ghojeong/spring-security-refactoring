package nextstep.security.access;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

// NOTE: https://github.com/spring-projects/spring-security/blob/main/web/src/main/java/org/springframework/security/web/access/AccessDeniedHandler.java
public interface AccessDeniedHandler {
    void handle(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;
}
