package org.ever._4ever_be_auth.config.oauth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;

// 하나 이상의 Bean 객체가 있는 경우의 각 Bean들을 인식하기 위해 등록하는 어노테이션
@Configuration
public class AuthorizationServerConfig {
    /**
     * {@code authorizationServerSecurityFilterChain}:
     * <ul>
     *     <il>인가 서버 전용 보안 필터 체인을 만들어 스프링 Security에 등록함.</il>
     *     <il>즉, OAuth 2.0 인가 서버의 엔드포인트를 처리할 보안 규칙을 세팅하는 것임.</il>
     * </ul>
     *
     */
    // 메서드가 반환하는 객체를 스프링 컨테이너에 빈으로 등록하는 어노테이션
    @Bean
    @Order(1) // Bean 객체의 적용 순서를 지정하는 어노테이션
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http,
                                                                      RegisteredClientRepository registeredClientRepository) throws Exception {
        // 인가 서버 표준 엔드포인트를 HttpSecurity에 붙일 때 필요한 세부 설정을 캡슐화 하기위한 객체
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();
        // 생성한 configurer가 자동으로 노출할 엔드포인트의 경로 패턴을 RequestMatcher 형태로 받음
        RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();

        http.securityMatcher(endpointsMatcher)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/.well-known/openid-configuration",
                                "/.well-known/jwks.json"
                        ).permitAll().anyRequest().authenticated())
                .csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
                .with(authorizationServerConfigurer
                        .oidc(Customizer.withDefaults()), Customizer.withDefaults());

        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }

    // Spring Authorization Server가 내부에서 사용하는 설정 값들을 묶어둔 객체로
    // 서버가 자신을 식별하고 각 엔드포인트 경로를 어떻게 노출할지 결정하는 정도를 가지고 있음.
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                //TODO: issuer는 게이트웨이를 통해 공개되는 url을 issuer로 사용해야함
                .issuer("http://localhost:8080")
                .authorizationEndpoint("/oauth2/authorize")
                .tokenEndpoint("/oauth2/token")
                .build();
    }

    // 인메모리 Registered Client 저장소 빈 정의 - 추후 DB 저장소로 교체 예정
    // PKCE 전용 공개 클라이언트
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient erpWebClient = RegisteredClient.withId("erp-web-client")
                .clientId("erp-web-client")
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE) // PKCE 전용 public client
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://localhost:3000/oauth2/callback")
                .scope(OidcScopes.OPENID)
                .scope("erp.scm.read")
                .tokenSettings(TokenSettings.builder().build())
                .clientSettings(ClientSettings.builder()
                        .requireProofKey(true)
                        .build())
                .build();

        return new InMemoryRegisteredClientRepository(erpWebClient);
    }
}
