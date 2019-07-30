package com.tjh.riskfactor.configs;

import lombok.val;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.Collections;

@Configuration
@EnableAuthorizationServer
@RequiredArgsConstructor
public class AuthorizeConfig extends AuthorizationServerConfigurerAdapter {

    @Value("${security.jwt.client-id}")
    private String clientId;

    @Value("${security.jwt.client-secret}")
    private String clientSecret;

    @Value("${security.jwt.grant-type}")
    private String grantType;

    @Value("${security.jwt.scope-read}")
    private String scopeRead;

    @Value("${security.jwt.scope-write}")
    private String scopeWrite;

    @Value("${security.jwt.resource-ids}")
    private String resourceIds;

    private final TokenStore tokenStore;
    private final JwtAccessTokenConverter accessTokenConverter;
    private final AuthenticationManager authenticationManager;

    @Override
    public void configure(ClientDetailsServiceConfigurer conf) throws Exception {
        conf.inMemory().withClient(this.clientId)
                .secret(this.clientSecret)
                .authorizedGrantTypes(this.grantType)
                .scopes(this.scopeRead, this.scopeWrite)
                .resourceIds(this.resourceIds);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer conf) throws Exception {
        val chain = new TokenEnhancerChain();
        chain.setTokenEnhancers(Collections.singletonList(accessTokenConverter));
        conf.tokenStore(tokenStore)
            .accessTokenConverter(accessTokenConverter)
            .tokenEnhancer(chain)
            .authenticationManager(authenticationManager);
    }

}
