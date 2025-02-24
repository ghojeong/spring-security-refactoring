package nextstep.security.config.annotation;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import nextstep.security.authentication.AuthenticationManager;
import nextstep.security.authentication.AuthenticationProvider;
import nextstep.security.config.Customizer;
import nextstep.security.config.DefaultSecurityFilterChain;
import nextstep.security.config.annotation.configurers.AuthorizeHttpRequestsConfigurer;
import nextstep.security.config.annotation.configurers.CsrfConfigurer;
import nextstep.security.config.annotation.configurers.FormLoginConfigurer;
import nextstep.security.config.annotation.configurers.HttpBasicConfigurer;
import nextstep.security.config.annotation.configurers.OAuth2LoginConfigurer;
import nextstep.security.config.annotation.configurers.SecurityConfigurer;
import nextstep.security.config.annotation.configurers.SecurityContextConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// NOTE: https://github.com/spring-projects/spring-security/blob/main/config/src/main/java/org/springframework/security/config/annotation/web/builders/HttpSecurity.java
public class HttpSecurity {
    private final LinkedHashMap<Class<? extends SecurityConfigurer>, SecurityConfigurer> configurers = new LinkedHashMap<>();
    private final Map<Class<?>, Object> sharedObjects = new HashMap<>();
    private final List<OrderedFilter> filters = new ArrayList<>();
    private final FilterOrderRegistration filterOrders = new FilterOrderRegistration();

    public HttpSecurity(AuthenticationManagerBuilder authenticationManagerBuilder, Map<Class<?>, Object> sharedObjects) {
        setSharedObject(AuthenticationManagerBuilder.class, authenticationManagerBuilder);
        sharedObjects.forEach((key, value) -> setSharedObject((Class<Object>) key, value));
    }

    private ApplicationContext getContext() {
        return getSharedObject(ApplicationContext.class);
    }

    public <C> C getSharedObject(Class<C> sharedType) {
        return (C) this.sharedObjects.get(sharedType);
    }

    public <C> void setSharedObject(Class<C> sharedType, C object) {
        this.sharedObjects.put(sharedType, object);
    }

    public DefaultSecurityFilterChain build() {
        setSharedObject(AuthenticationManager.class, getAuthenticationRegistry().build());
        for (SecurityConfigurer configurer : configurers.values()) {
            configurer.init(this);
        }
        for (SecurityConfigurer configurer : configurers.values()) {
            configurer.configure(this);
        }
        filters.sort(OrderComparator.INSTANCE);
        final List<Filter> sortedFilters = new ArrayList<>(filters.size());
        for (Filter filter : filters) {
            sortedFilters.add(((OrderedFilter) filter).filter);
        }
        return new DefaultSecurityFilterChain(sortedFilters);
    }

    public HttpSecurity addFilter(Filter filter) {
        filters.add(new OrderedFilter(
                filter, filterOrders.getOrder(filter.getClass())
        ));
        return this;
    }

    public HttpSecurity authenticationProvider(AuthenticationProvider authenticationProvider) {
        getAuthenticationRegistry().authenticationProvider(authenticationProvider);
        return this;
    }

    private AuthenticationManagerBuilder getAuthenticationRegistry() {
        return getSharedObject(AuthenticationManagerBuilder.class);
    }

    public HttpSecurity csrf(Customizer<CsrfConfigurer> csrfCustomizer) {
        csrfCustomizer.customize(getOrApply(new CsrfConfigurer()));
        return this;
    }

    public HttpSecurity httpBasic(Customizer<HttpBasicConfigurer> httpBasicCustomizer) {
        httpBasicCustomizer.customize(getOrApply(new HttpBasicConfigurer()));
        return this;
    }

    public HttpSecurity formLogin(Customizer<FormLoginConfigurer> formLoginCustomizer) {
        formLoginCustomizer.customize(getOrApply(new FormLoginConfigurer()));
        return this;
    }

    public HttpSecurity oauth2Login(Customizer<OAuth2LoginConfigurer> oauth2LoginCustomizer) {
        oauth2LoginCustomizer.customize(getOrApply(new OAuth2LoginConfigurer()));
        return this;
    }

    public HttpSecurity authorizeHttpRequests(
            Customizer<AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry> authorizeHttpRequestsCustomizer
    ) {
        authorizeHttpRequestsCustomizer.customize(getOrApply(
                new AuthorizeHttpRequestsConfigurer(getContext())
        ).getRegistry());
        return this;
    }

    public HttpSecurity securityContext(Customizer<SecurityContextConfigurer> securityContextCustomizer) {
        securityContextCustomizer.customize(getOrApply(new SecurityContextConfigurer()));
        return this;
    }

    private <C extends SecurityConfigurer> C getOrApply(C configurer) {
        final Class<? extends SecurityConfigurer> clazz = configurer.getClass();
        final C existingConfig = (C) configurers.get(clazz);
        if (existingConfig != null) {
            return existingConfig;
        }
        configurers.put(clazz, configurer);
        return configurer;
    }

    private record OrderedFilter(Filter filter, int order) implements Ordered, Filter {
        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            filter.doFilter(servletRequest, servletResponse, filterChain);
        }

        @Override
        public int getOrder() {
            return order;
        }
    }
}
