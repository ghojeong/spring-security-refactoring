package nextstep.security.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.boot.web.servlet.DelegatingFilterProxyRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnDefaultWebSecurity
public class SecurityFilterAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(DelegatingFilterProxyRegistrationBean.class)
    public DelegatingFilterProxyRegistrationBean securityFilterChainRegistration() {
        return new DelegatingFilterProxyRegistrationBean("springSecurityFilterChain");
    }
}
