package nextstep.security.autoconfigure;

import nextstep.oauth2.OAuth2ClientProperties;
import nextstep.oauth2.registration.ClientRegistration;
import nextstep.oauth2.registration.ClientRegistrationRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.HashMap;
import java.util.Map;

@Configuration(proxyBeanMethods = false)
@ConditionalOnDefaultWebSecurity
@EnableConfigurationProperties(OAuth2ClientProperties.class)
class OAuth2ClientRegistrationRepositoryConfiguration {
    @Bean
    @ConditionalOnMissingBean(ClientRegistrationRepository.class)
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    ClientRegistrationRepository clientRegistrationRepository(OAuth2ClientProperties properties) {
        final Map<String, ClientRegistration> clientRegistrations = new HashMap<>();
        properties.getRegistration().forEach((registrationId, registration) -> {
            final OAuth2ClientProperties.Provider provider = properties.getProvider().get(registrationId);
            clientRegistrations.put(registrationId, new ClientRegistration(
                    registrationId,
                    registration.getClientId(),
                    registration.getClientSecret(),
                    registration.getRedirectUri(),
                    registration.getScope(),
                    provider.getAuthorizationUri(),
                    provider.getTokenUri(),
                    provider.getUserInfoUri(),
                    provider.getUserNameAttributeName()
            ));
        });
        return new ClientRegistrationRepository(clientRegistrations);
    }
}
