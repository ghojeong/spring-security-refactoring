package nextstep.security.config.annotation;

import jakarta.servlet.Filter;
import nextstep.security.authentication.BasicAuthenticationFilter;
import nextstep.security.authentication.UsernamePasswordAuthenticationFilter;
import nextstep.security.authorization.AuthorizationFilter;
import nextstep.security.context.SecurityContextHolderFilter;
import nextstep.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CorsFilter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// NOTE: https://github.com/spring-projects/spring-security/blob/main/config/src/main/java/org/springframework/security/config/annotation/web/builders/FilterOrderRegistration.java
public class FilterOrderRegistration {
    private static final int INITIAL_ORDER = 100;
    private static final int ORDER_STEP = 100;
    private final Map<String, Integer> filterToOrder = new HashMap<>();

    FilterOrderRegistration() {
        final Step order = new Step(INITIAL_ORDER, ORDER_STEP);
        order.next();
        put(SecurityContextHolderFilter.class, order.next());
        put(CorsFilter.class, order.next());
        put(CsrfFilter.class, order.next());
        filterToOrder.put(
                "nextstep.oauth2.web.OAuth2AuthorizationRequestRedirectFilter",
                order.next()
        );
        filterToOrder.put(
                "nextstep.oauth2.web.OAuth2LoginAuthenticationFilter",
                order.next()
        );
        put(UsernamePasswordAuthenticationFilter.class, order.next());
        order.next();
        put(BasicAuthenticationFilter.class, order.next());
        put(AuthorizationFilter.class, order.next());
    }

    void put(Class<? extends Filter> filter, int position) {
        filterToOrder.putIfAbsent(filter.getName(), position);
    }

    Integer getOrder(Class<?> clazz) {
        while (clazz != null) {
            final Integer result = filterToOrder.get(clazz.getName());
            if (result != null) {
                return result;
            }
            clazz = clazz.getSuperclass();
        }
        return getMaxOrder() + 1;
    }

    private Integer getMaxOrder() {
        return filterToOrder.isEmpty() ? 0
                : Collections.max(filterToOrder.values());
    }

    private static class Step {
        private final int stepSize;
        private int value;

        Step(int initialValue, int stepSize) {
            this.value = initialValue;
            this.stepSize = stepSize;
        }

        int next() {
            int oldValue = this.value;
            this.value += this.stepSize;
            return oldValue;
        }
    }
}
