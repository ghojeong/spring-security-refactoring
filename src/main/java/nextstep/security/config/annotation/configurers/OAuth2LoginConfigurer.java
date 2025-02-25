package nextstep.security.config.annotation.configurers;

import nextstep.oauth2.authentication.OAuth2LoginAuthenticationProvider;
import nextstep.oauth2.registration.ClientRegistrationRepository;
import nextstep.oauth2.userinfo.OAuth2UserService;
import nextstep.oauth2.web.OAuth2AuthorizationRequestRedirectFilter;
import nextstep.oauth2.web.OAuth2AuthorizedClientRepository;
import nextstep.oauth2.web.OAuth2LoginAuthenticationFilter;
import nextstep.security.authentication.AuthenticationManager;
import nextstep.security.config.annotation.HttpSecurity;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import java.util.Map;

// NOTE: https://github.com/spring-projects/spring-security/blob/main/config/src/main/java/org/springframework/security/config/annotation/web/configurers/oauth2/client/OAuth2LoginConfigurer.java
public class OAuth2LoginConfigurer implements SecurityConfigurer {
    @Override
    public void init(HttpSecurity http) {
        http.authenticationProvider(new OAuth2LoginAuthenticationProvider(
                getOAuth2UserService(http)
        ));
        final OAuth2LoginAuthenticationFilter authFilter = new OAuth2LoginAuthenticationFilter(
                getClientRegistrationRepository(http), getAuthorizedClientRepository(http), null
        );
        authFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        http.addFilter(authFilter);
    }

    @Override
    public void configure(HttpSecurity http) {
        http.addFilter(new OAuth2AuthorizationRequestRedirectFilter(
                getClientRegistrationRepository(http)
        ));
    }

    private OAuth2UserService getOAuth2UserService(HttpSecurity http) {
        return http.getSharedObject(ApplicationContext.class)
                .getBean(OAuth2UserService.class);
    }

    public ClientRegistrationRepository getClientRegistrationRepository(HttpSecurity builder) {
        final ClientRegistrationRepository clientRegistrationRepository = builder.getSharedObject(ClientRegistrationRepository.class);
        return clientRegistrationRepository != null
                ? clientRegistrationRepository
                : getClientRegistrationRepositoryBean(builder);
    }

    private ClientRegistrationRepository getClientRegistrationRepositoryBean(HttpSecurity builder) {
        return builder.getSharedObject(ApplicationContext.class)
                .getBean(ClientRegistrationRepository.class);
    }

    public OAuth2AuthorizedClientRepository getAuthorizedClientRepository(HttpSecurity builder) {
        final OAuth2AuthorizedClientRepository authorizedClientRepository = builder
                .getSharedObject(OAuth2AuthorizedClientRepository.class);
        return authorizedClientRepository != null
                ? authorizedClientRepository
                : getAuthorizedClientRepositoryBean(builder);
    }

    private OAuth2AuthorizedClientRepository getAuthorizedClientRepositoryBean(HttpSecurity builder) {
        final Map<String, OAuth2AuthorizedClientRepository> authorizedClientRepositoryMap = BeanFactoryUtils
                .beansOfTypeIncludingAncestors(
                        builder.getSharedObject(ApplicationContext.class),
                        OAuth2AuthorizedClientRepository.class
                );
        if (authorizedClientRepositoryMap.size() > 1) {
            throw new NoUniqueBeanDefinitionException(
                    OAuth2AuthorizedClientRepository.class,
                    authorizedClientRepositoryMap.size(),
                    "OAuth2AuthorizedClientRepository bean is not unique"
            );
        }
        return authorizedClientRepositoryMap.isEmpty()
                ? new OAuth2AuthorizedClientRepository()
                : authorizedClientRepositoryMap.values().iterator().next();
    }
}
