package nextstep.oauth2.registration;

import nextstep.oauth2.OAuth2ClientProperties;

import java.util.HashMap;
import java.util.Map;

public class ClientRegistrationRepository {
    private final Map<String, ClientRegistration> registrations;

    public ClientRegistrationRepository(Map<String, ClientRegistration> registrations) {
        this.registrations = registrations;
    }

    public static ClientRegistrationRepository of(OAuth2ClientProperties properties) {
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

    public ClientRegistration findByRegistrationId(String registrationId) {
        return this.registrations.get(registrationId);
    }
}
